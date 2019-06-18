// Solution to Week 13 Homework Exercise 4

// compile: javac -cp json-simple-1.1.1.jar;. SubscribeRequest.java

import org.json.simple.*;  // required for JSON encoding and decoding

public class SubscribeRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
        SubscribeRequest.class.getSimpleName();

    public SubscribeRequest() {
    }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        //obj.put("message", message);
        return obj;
    }

    // Tries to deserialize a SubscribeRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static SubscribeRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            // construct the object to return (checking for nulls)
            return new SubscribeRequest();
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
