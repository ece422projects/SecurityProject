public class Permissions {

    private User owner;
    private ArrayList<User> viewUsers;

    public Permissions(User o) {
        this.owner = o;
        this.rwUsers = new ArrayList<User>();
    }

    public void addViewUser(User u) {
        this.viewUsers.add(u);
    }

    public void removeViewUser(User u) {
        this.viewUsers.remove(u);
    }

    public Boolean canView(User u) {
        return this.viewUsers.contains(u);
    }

    public Boolean canReadWrite(User u) {
        return (this.owner == u);
    }
}