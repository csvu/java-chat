package mop.app.client;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("SELECT\n" +
                    "\tCO.CONVERSATION_ID AS \"id\",\n" +
                    "\tCO.NAME AS DISPLAY_NAME,\n" +
                    "\tCO.ICON AS AVATAR,\n" +
                    "\tCO_TYPE.TYPE_NAME AS \"type\"\n" +
                    "FROM\n" +
                    "\tPUBLIC.ENROLLMENT AS EN\n" +
                    "\tJOIN PUBLIC.CONVERSATION AS CO ON EN.CONVERSATION_ID = CO.CONVERSATION_ID\n" +
                    "\tJOIN PUBLIC.CONVERSATION_TYPE AS CO_TYPE ON CO.TYPE_ID = CO_TYPE.TYPE_ID\n" +
                    "WHERE\n" +
                    "\tEN.USER_ID = ?");
            Client.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}