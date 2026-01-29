package Backend;

import com.google.gson.*;

import java.lang.reflect.Type;

public class UserAdapter implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String role = jsonObject.get("role").getAsString();

        if (role.equalsIgnoreCase("Instructor"))
            return context.deserialize(json, Instructor.class);

        else if (role.equalsIgnoreCase("Student"))
            return context.deserialize(json, Student.class);

        else if (role.equalsIgnoreCase("Admin"))
            return context.deserialize(json, Admin.class);

        return context.deserialize(json, User.class);
    }
}
