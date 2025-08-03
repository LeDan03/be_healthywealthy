package vn.edu.stu.PostService.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.PostService.exception.BadRequestException;
import vn.edu.stu.PostService.exception.NotFoundException;
import vn.edu.stu.PostService.mapper.CategoryMapper;
import vn.edu.stu.PostService.model.Category;
import vn.edu.stu.PostService.repository.CategoryRepo;
import vn.edu.stu.PostService.repository.RecipeRepo;
import vn.edu.stu.PostService.request.CategoryRequest;
import vn.edu.stu.PostService.response.CategoryResponse;
import vn.edu.stu.PostService.response.RecipeResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;
    private final RecipeRepo recipeRepo;
    public static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    @PostConstruct
    private void defaultCategory() {
        addDefaultCategories();
    }

    public void addDefaultCategories() {
        List<Category> defaultCategories = List.of(
                new Category("Tráng miệng", "https://i.ytimg.com/vi/FWtM00N_iOQ/maxresdefault.jpg"),
                new Category("Chay",
                        "https://cdn.tgdd.vn/Files/2016/08/02/868560/cach-lam-2-mon-chay-thom-ngon-tu-nam-4.jpg"),
                new Category("Canh",
                        "http://chef.com.vn/wp-content/uploads/2015/07/cach-lam-cac-mon-canh-chay-don-gian..jpg"),
                new Category("Xào", "https://i.ytimg.com/vi/Z-2DBXPsg2M/maxresdefault.jpg"),
                new Category("Hấp",
                        "https://cdn.tgdd.vn/Files/2020/02/14/1236363/ca-hap-xi-dau-thom-ngot-cuc-ky-de-lam-202103251602566288.jpg"),
                new Category("Nướng", "https://media.vov.vn/sites/default/files/styles/large/public/2020-10/bo_2.jpg"),
                new Category("Chiên",
                        "http://cdn.tgdd.vn/Files/2019/10/12/1207784/cach-pha-bot-chien-gion-ngon-nhu-nha-hang-cho-tung-mon-an-202206031447329169.jpeg"),
                new Category("Kho",
                        "https://kienthucamthuc.net/wp-content/uploads/2021/01/cah-lam-thit-kho-tau-ngon.jpg"),
                new Category("Mặn",
                        "https://tse1.mm.bing.net/th/id/OIP.erarozm_uhzTK_HT6334eAHaE8?rs=1&pid=ImgDetMain&cb=idpwebpc2"));

        List<Category> categoriesToSave = new ArrayList<>();

        for (Category category : defaultCategories) {
            if (!categoryRepo.existsByName(category.getName())) {
                categoriesToSave.add(category);
            }
        }

        if (!categoriesToSave.isEmpty()) {
            categoryRepo.saveAll(categoriesToSave);
        }
    }

    public void saveNewCategory(Category category) {
        if (categoryRepo.existsByName(category.getName())) {
            return;
        }
        categoryRepo.save(category);
    }

    public CategoryResponse getCategoryById(int id) {
        Category category = categoryRepo.findById(id).orElse(null);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return categoryMapper.toResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        LOGGER.info("Đang get all categories {category service}");
        return categoryMapper.toResponseList(categoryRepo.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepo.existsByName(categoryRequest.getName())) {
            throw new BadRequestException("Category name already exists");
        }
        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);
        return categoryMapper.toResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategoryById(int id) {
        if (categoryRepo.existsById(id)) {
            if (recipeRepo.isUsedCategory(id)) {
                throw new BadRequestException("Phân loại đã được sử dụng, không thể xóa");
            }
            categoryRepo.deleteById(id);
        } else {
            throw new NotFoundException("Category not found");
        }
    }

}
