package vn.edu.stu.WebBlogNauAn.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.WebBlogNauAn.dto.SaveRecipeDto;
import vn.edu.stu.WebBlogNauAn.model.SavedRecipe;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaveRecipeMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public SaveRecipeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SaveRecipeDto toDto(SavedRecipe savedRecipe) {
        return new SaveRecipeDto(savedRecipe.getRecipeId(), savedRecipe.getAccount().getId(), savedRecipe.getSaveAt());
    }
    public List<SaveRecipeDto> toDtoList(List<SavedRecipe> savedRecipes) {
        return savedRecipes.stream().map(this::toDto).collect(Collectors.toList());
    }
}
