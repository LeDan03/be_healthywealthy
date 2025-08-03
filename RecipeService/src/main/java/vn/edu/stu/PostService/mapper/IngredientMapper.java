package vn.edu.stu.PostService.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.PostService.model.Ingredient;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.model.Unit;
import vn.edu.stu.PostService.repository.UnitRepo;
import vn.edu.stu.PostService.request.IngredientRequest;
import vn.edu.stu.PostService.response.IngredientResponse;

import java.lang.module.ModuleDescriptor.Builder;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngredientMapper {
    private final UnitRepo unitRepo;

    @Autowired
    public IngredientMapper(UnitRepo unitRepo) {
        this.unitRepo = unitRepo;
    }

    public Ingredient toIngredient(IngredientRequest ingredientRequest, Recipe recipe) {
        Unit unit = unitRepo.findById(ingredientRequest.getUnitId()).orElseThrow(()->new RuntimeException("Unit not found"));
        // Ingredient ingredient = new Ingredient();
        // ingredient.setName(ingredientRequest.getName());
        // ingredient.setAmount(ingredientRequest.getAmount());
        // ingredient.setUnit(unit);
        // return ingredient;
        return Ingredient.builder()
        .amount(ingredientRequest.getAmount())
        .name(ingredientRequest.getName())
        .unit(unit)
        .recipe(recipe)
        .build();
    }

    public List<Ingredient> toIngredientList(List<IngredientRequest> ingredientRequestList, Recipe recipe) {
        return ingredientRequestList.stream().map(request->toIngredient(request, recipe)).collect(Collectors.toList());
    }
    public IngredientResponse toResponse(Ingredient ingredient){
        return IngredientResponse.builder()
                .amount(ingredient.getAmount())
                .name(ingredient.getName())
                .unitId(ingredient.getUnit().getId())
                .build();
    }

    public List<IngredientResponse> toIngredients(List<Ingredient> ingredients){
        return ingredients.stream().map(this::toResponse).toList();
    }
}
