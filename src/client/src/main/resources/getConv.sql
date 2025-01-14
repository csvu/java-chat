SELECT DISTINCT
	CO.CONVERSATION_ID AS "id",
	CASE
		WHEN CO_TYPE.TYPE_NAME = 'PAIR' THEN (
			SELECT
				PARTNER.DISPLAY_NAME
			FROM
				PUBLIC.ENROLLMENT AS EN
				JOIN PUBLIC."user" AS PARTNER ON EN.USER_ID = PARTNER.USER_ID
			WHERE
				EN.USER_ID <> ?
				AND CONVERSATION_ID = CO.CONVERSATION_ID
			LIMIT
				1
		)
		ELSE CO.NAME
	END AS DISPLAY_NAME,
	CO.ICON AS AVATAR,
	CO_TYPE.TYPE_NAME AS "type",
	EN.IS_SEEN AS IS_SEEN,
	MSG.*
FROM
	PUBLIC.ENROLLMENT AS EN
	JOIN PUBLIC.CONVERSATION AS CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID
	JOIN PUBLIC.CONVERSATION_TYPE AS CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID
	LEFT JOIN (
		SELECT
			R.conversation_id as conversation_id, me.msg_id as msg_id, me.user_id as sender_id, me.sent_at as sent_at, me.content as "content"
		FROM
			(
				SELECT
					ME.CONVERSATION_ID,
					MAX(ME.MSG_ID) as MSG_ID
				FROM
					PUBLIC.MESSAGE AS ME LEFT JOIN PUBLIC.HIDDEN_MESSAGE AS HM ON ME.MSG_ID = HM.MSG_ID AND HM.USER_ID = ?
				WHERE
				    HM.MSG_ID IS NULL
				GROUP BY
					CONVERSATION_ID
			) AS R JOIN PUBLIC.MESSAGE as ME on R.msg_id =  me.msg_id
	) AS MSG ON MSG.CONVERSATION_ID = CO.CONVERSATION_ID
WHERE
	EN.USER_ID = ?
--	AND (
--		CO_TYPE.TYPE_NAME <> 'PAIR'
--		OR EXISTS (
--			SELECT
--				*
--			FROM
--				PUBLIC.RELATIONSHIP
--				JOIN PUBLIC.RELATIONSHIP_TYPE ON STATUS = TYPE_ID
--			WHERE
--				USER_ID1 = (
--					SELECT
--						USER_ID
--					FROM
--						PUBLIC.ENROLLMENT
--					WHERE
--						USER_ID <> ?
--						AND CONVERSATION_ID = CO.CONVERSATION_ID
--					LIMIT
--						1
--				)
--				AND USER_ID2 = ?
--				AND TYPE_NAME = 'FRIEND'
--		)
--	)
ORDER BY MSG.SENT_AT DESC NULLS LAST