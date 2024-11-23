--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1
-- Dumped by pg_dump version 16.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: chat; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE chat WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';


ALTER DATABASE chat OWNER TO postgres;

\connect chat

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: conversation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conversation (
    conversation_id integer NOT NULL,
    name character varying,
    icon character varying,
    type_id integer
);


ALTER TABLE public.conversation OWNER TO postgres;

--
-- Name: conversation_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conversation_type (
    type_id integer NOT NULL,
    type_name character varying
);


ALTER TABLE public.conversation_type OWNER TO postgres;

--
-- Name: enrollment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.enrollment (
    user_id integer NOT NULL,
    conversation_id integer NOT NULL,
    role_id integer
);


ALTER TABLE public.enrollment OWNER TO postgres;

--
-- Name: enrollment_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.enrollment_role (
    role_id integer NOT NULL,
    role_name character varying
);


ALTER TABLE public.enrollment_role OWNER TO postgres;

--
-- Name: login_time; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.login_time (
    instance integer NOT NULL,
    login_at timestamp without time zone,
    user_id integer
);


ALTER TABLE public.login_time OWNER TO postgres;

--
-- Name: message; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.message (
    msg_id integer NOT NULL,
    conversation_id integer,
    user_id integer,
    sent_at timestamp without time zone,
    content character varying
);


ALTER TABLE public.message OWNER TO postgres;

--
-- Name: open_time; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.open_time (
    instance integer NOT NULL,
    open_at timestamp without time zone,
    user_id integer
);


ALTER TABLE public.open_time OWNER TO postgres;

--
-- Name: relationship; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relationship (
    user_id1 integer NOT NULL,
    user_id2 integer NOT NULL,
    created_at timestamp without time zone,
    status integer
);


ALTER TABLE public.relationship OWNER TO postgres;

--
-- Name: relationship_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relationship_type (
    type_id integer NOT NULL,
    type_name character varying
);


ALTER TABLE public.relationship_type OWNER TO postgres;

--
-- Name: report; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.report (
    report_id integer NOT NULL,
    user_id1 integer,
    user_id2 integer,
    created_at timestamp without time zone
);


ALTER TABLE public.report OWNER TO postgres;

--
-- Name: user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."user" (
    user_id integer NOT NULL,
    username character varying,
    display_name character varying,
    password character varying,
    email character varying,
    avatar character varying,
    birth_date date,
    address character varying,
    is_active boolean,
    is_banned boolean,
    role_id integer,
    created_at timestamp without time zone
);


ALTER TABLE public."user" OWNER TO postgres;

--
-- Name: user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_role (
    role_id integer NOT NULL,
    role_name character varying
);


ALTER TABLE public.user_role OWNER TO postgres;

--
-- Data for Name: conversation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.conversation (conversation_id, name, icon, type_id) FROM stdin;
1	General Chat	icon1.png	1
2	Private Chat	icon2.png	2
3	Work Channel	icon3.png	2
\.


--
-- Data for Name: conversation_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.conversation_type (type_id, type_name) FROM stdin;
1	Group Chat
2	Direct Message
\.


--
-- Data for Name: enrollment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.enrollment (user_id, conversation_id, role_id) FROM stdin;
1	1	1
2	1	2
3	2	2
\.


--
-- Data for Name: enrollment_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.enrollment_role (role_id, role_name) FROM stdin;
1	Admin
2	Member
\.


--
-- Data for Name: login_time; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.login_time (instance, login_at, user_id) FROM stdin;
1	2024-11-17 08:00:00	1
2	2024-11-17 08:15:00	2
3	2024-11-17 08:30:00	3
\.


--
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message (msg_id, conversation_id, user_id, sent_at, content) FROM stdin;
1	1	1	2024-11-17 13:00:00	Hello
2	1	2	2024-11-17 13:05:00	Hi there
3	2	3	2024-11-17 13:10:00	Hey
\.


--
-- Data for Name: open_time; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.open_time (instance, open_at, user_id) FROM stdin;
1	2024-11-17 09:00:00	1
2	2024-11-17 09:30:00	2
3	2024-11-17 10:00:00	3
\.


--
-- Data for Name: relationship; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.relationship (user_id1, user_id2, created_at, status) FROM stdin;
1	2	2024-11-17 14:00:00	1
1	3	2024-11-17 14:15:00	2
2	3	2024-11-17 14:30:00	3
\.


--
-- Data for Name: relationship_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.relationship_type (type_id, type_name) FROM stdin;
1	Friend
2	Blocked
3	Pending
\.


--
-- Data for Name: report; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.report (report_id, user_id1, user_id2, created_at) FROM stdin;
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."user" (user_id, username, display_name, password, email, avatar, birth_date, address, is_active, is_banned, role_id, created_at) FROM stdin;
1	john_doe	John Doe	password123	john@example.com	avatar1.png	1990-01-01	123 Main St	t	f	1	2024-11-17 10:00:00
2	jane_doe	Jane Doe	password456	jane@example.com	avatar2.png	1992-05-15	456 Oak St	t	f	2	2024-11-17 11:00:00
3	sam_smith	Sam Smith	password789	sam@example.com	avatar3.png	1985-08-23	789 Pine St	f	t	3	2024-11-17 12:00:00
\.


--
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_role (role_id, role_name) FROM stdin;
1	Admin
2	User
3	Moderator
\.


--
-- Name: conversation conversation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conversation
    ADD CONSTRAINT conversation_pkey PRIMARY KEY (conversation_id);


--
-- Name: conversation_type conversation_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conversation_type
    ADD CONSTRAINT conversation_type_pkey PRIMARY KEY (type_id);


--
-- Name: enrollment enrollment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enrollment
    ADD CONSTRAINT enrollment_pkey PRIMARY KEY (user_id, conversation_id);


--
-- Name: enrollment_role enrollment_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enrollment_role
    ADD CONSTRAINT enrollment_role_pkey PRIMARY KEY (role_id);


--
-- Name: login_time login_time_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.login_time
    ADD CONSTRAINT login_time_pkey PRIMARY KEY (instance);


--
-- Name: message message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (msg_id);


--
-- Name: open_time open_time_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.open_time
    ADD CONSTRAINT open_time_pkey PRIMARY KEY (instance);


--
-- Name: relationship relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationship
    ADD CONSTRAINT relationship_pkey PRIMARY KEY (user_id1, user_id2);


--
-- Name: relationship_type relationship_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationship_type
    ADD CONSTRAINT relationship_type_pkey PRIMARY KEY (type_id);


--
-- Name: report report_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report
    ADD CONSTRAINT report_pkey PRIMARY KEY (report_id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (user_id);


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (role_id);


--
-- Name: conversation conversation_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conversation
    ADD CONSTRAINT conversation_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.conversation_type(type_id);


--
-- Name: enrollment enrollment_conversation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enrollment
    ADD CONSTRAINT enrollment_conversation_id_fkey FOREIGN KEY (conversation_id) REFERENCES public.conversation(conversation_id);


--
-- Name: enrollment enrollment_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enrollment
    ADD CONSTRAINT enrollment_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.enrollment_role(role_id);


--
-- Name: enrollment enrollment_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enrollment
    ADD CONSTRAINT enrollment_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id);


--
-- Name: login_time login_time_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.login_time
    ADD CONSTRAINT login_time_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id);


--
-- Name: message message_conversation_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_conversation_id_fkey FOREIGN KEY (conversation_id) REFERENCES public.conversation(conversation_id);


--
-- Name: message message_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id);


--
-- Name: open_time open_time_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.open_time
    ADD CONSTRAINT open_time_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(user_id);


--
-- Name: relationship relationship_status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationship
    ADD CONSTRAINT relationship_status_fkey FOREIGN KEY (status) REFERENCES public.relationship_type(type_id);


--
-- Name: relationship relationship_user_id1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationship
    ADD CONSTRAINT relationship_user_id1_fkey FOREIGN KEY (user_id1) REFERENCES public."user"(user_id);


--
-- Name: relationship relationship_user_id2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationship
    ADD CONSTRAINT relationship_user_id2_fkey FOREIGN KEY (user_id2) REFERENCES public."user"(user_id);


--
-- Name: report report_user_id1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report
    ADD CONSTRAINT report_user_id1_fkey FOREIGN KEY (user_id1) REFERENCES public."user"(user_id);


--
-- Name: report report_user_id2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report
    ADD CONSTRAINT report_user_id2_fkey FOREIGN KEY (user_id2) REFERENCES public."user"(user_id);


--
-- Name: user user_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.user_role(role_id);


--
-- PostgreSQL database dump complete
--

