// Solution to Week 13 Exercise 2 and 3

// compile: javac -cp json-simple-1.1.1.jar;. Message.java

import org.json.simple.*;  // required for JSON encoding and decoding

public class Message {
    // class name to be used as tag in JSON representation
    private static final String _class =
        Message.class.getSimpleName();

    private final String body;
    private final String from;
    private final long when;

    // Constructor; throws NullPointerException if arguments are null
    public Message(String body, String from, long when) {
        if (body == null || from == null)
            throw new NullPointerException();
        this.body      = body;
        this.from    = from;
        this.when = when;
    }

    public String getBody()      { return body; }
    public String getFrom()    { return from; }
    public long   getWhen() { return when; }

    public String toString() {
        return from + ": " + body + " (" + when + ")";
    }

    //////////////////////////////////////////////////////////////////////////
    // JSON representation

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class",    _class);
        obj.put("body",      body);
        obj.put("from",    from);
        obj.put("when", when);
        return obj;
    }

    // Tries to deserialize a Message instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static Message fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize message fields (checking when for null)
            String body      = (String)obj.get("body");
            String from    = (String)obj.get("from");
            long   when = (long)obj.get("when");
            // construct the object to return (checking for nulls)
            return new Message(body, from, when);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
