public class SecureFileSystem {

    public SecureFileSystem() {

    }

    public static void main (String[] args) {

        TerminalCommand tc = new TerminalCommand();

        Group team1 = new Group("team1");
        Group team2 = new Group("team2");
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");

        team1.addGroupMember(user1);
        team1.addGroupMember(user2);
        team2.addGroupMember(user3);

        tc.addUser(user1);
        tc.addUser(user2);
        tc.addUser(user3);
        tc.changeDirectory(user1, "user1");
        tc.writeToFile(user1, "Words.txt", "These are my words. I like them.");
        tc.writeToFile(user1, "Sentences.txt", "Please stop!");
        tc.writeToFile(user1, "Words.txt", "These are my new words.");
        tc.readFile(user1, "Words.txt");
        tc.returnFiles(user1);
        tc.changeDirectory(user2, "user2");
        tc.writeToFile(user2, "Words1.txt", "I like them.");
        tc.readFile(user2, "Words1.txt");
        tc.returnFiles(user2);
        tc.changeDirectory(user1, "user1");
        tc.returnFiles(user1);
    }
}