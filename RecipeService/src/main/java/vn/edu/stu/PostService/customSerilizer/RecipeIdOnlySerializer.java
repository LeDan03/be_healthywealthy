package vn.edu.stu.PostService.customSerilizer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import vn.edu.stu.PostService.model.Recipe;

import java.io.IOException;

public class RecipeIdOnlySerializer extends JsonSerializer<Recipe> {
    @Override
    public void serialize(Recipe recipe, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (recipe != null) {
            gen.writeStartObject();
            gen.writeNumberField("id", recipe.getId());
            gen.writeEndObject();
        } else {
            gen.writeNull();
        }
    }
}