package vn.edu.stu.PostService.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.edu.stu.PostService.exception.BadRequestException;
import vn.edu.stu.PostService.exception.ConflictException;
import vn.edu.stu.PostService.mapper.UnitMapper;
import vn.edu.stu.PostService.model.Unit;
import vn.edu.stu.PostService.repository.IngredientRepo;
import vn.edu.stu.PostService.repository.UnitRepo;
import vn.edu.stu.PostService.request.UnitRequest;
import vn.edu.stu.PostService.response.UnitResponse;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepo unitRepo;
    private final IngredientRepo ingredientRepo;
    private final UnitMapper unitMapper;

    @PostConstruct
    public void init() {
        Unit gam = new Unit();
        gam.setName("gram");
        Unit lit = new Unit();
        lit.setName("lít");
        Unit kg = new Unit();
        kg.setName("kg");
        Unit lang = new Unit();
        lang.setName("lạng");
        Unit ml = new Unit();
        ml.setName("ml");
        Unit mg = new Unit();
        mg.setName("mg");
        Unit muong = new Unit();
        muong.setName("muỗng");
        Unit chen = new Unit();
        chen.setName("chén");
        Unit con = new Unit();
        con.setName("con");
        Unit cai = new Unit();
        cai.setName("cái");

        if (!unitRepo.existsById(1)) {
            unitRepo.save(gam);
            unitRepo.save(lit);
            unitRepo.save(kg);
            unitRepo.save(lang);
            unitRepo.save(ml);
            unitRepo.save(mg);
            unitRepo.save(muong);
            unitRepo.save(chen);
            unitRepo.save(con);
            unitRepo.save(cai);
        }
    }

    public void createUnit(UnitRequest request) {
        if (unitRepo.existsByName(request.getName())) {
            throw new ConflictException("Đơn vị '" + request.getName() + "' đã tồn tại rồi");
        }
        Unit unit = new Unit();
        unit.setName(request.getName());
        unitRepo.save(unit);
    }

    public void deleteById(int id) {
        Optional<Unit> unitOpt = unitRepo.findById(id);
        if (!unitOpt.isPresent()) {
            throw new NotFoundException("Đơn vị không tồn tại");
        }

        if (ingredientRepo.isUsedUnit(id)) {
            throw new BadRequestException("Đơn vị đã được sử dụng, không thể xóa");
        }
        unitRepo.deleteById(id);
    }

    public List<UnitResponse> getAllUnits() {
        return unitMapper.toResponseList(unitRepo.findAll());
    }
}
