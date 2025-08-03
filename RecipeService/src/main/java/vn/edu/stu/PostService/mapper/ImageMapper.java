package vn.edu.stu.PostService.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.dto.UpdateImageDto;
import vn.edu.stu.PostService.model.Image;
import vn.edu.stu.PostService.model.Recipe;
import vn.edu.stu.PostService.response.ImageResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageMapper {
    private final ModelMapper modelMapper;

    public Image toImage(UpdateImageDto updateImageDto, Recipe recipe) {
        return Image.builder()
                .url(updateImageDto.getSecureUrl())
                .publicId(updateImageDto.getPublicId())
                .recipe(recipe)
                .build();
    }

    public List<Image> toImageList(List<UpdateImageDto> dtos, Recipe recipe) {
        List<Image> images = new ArrayList<Image>();
        for (UpdateImageDto dto : dtos) {
            images.add(toImage(dto, recipe));
        }
        return images;
    }

    public ImageResponse toReponse(Image image) {
        return modelMapper.map(image, ImageResponse.class);
    }

    public List<ImageResponse> toResponses(List<Image> images) {
        return images.stream().map(this::toReponse).toList();
    }
}
