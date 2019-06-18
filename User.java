//import org.json.simple.*;  // required for JSON encoding and decoding
import java.util.List;

public class User {
    private String name;
    private List<User> subList;

    public User(String name, List<User> subList) {
        this.name = name;
        this.subList = subList;
    }

    public String getName() { return name; }
    public List<User> getSubList() { return subList; }
    public void addToSubList(User user) {
        subList.add(user);
    }
    public void deleteFromSubList(User user) {
        subList.remove(user);
    }
}