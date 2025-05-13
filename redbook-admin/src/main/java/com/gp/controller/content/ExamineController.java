package com.gp.controller.content;


import com.gp.dto.req.content.ExamineDo;
import com.gp.result.Result;
import com.gp.service.TReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExamineController {

    private final TReviewsService reviewsService;



    /**
     * 视频审核通过
     */
    @PostMapping("/api/content-service/audit")
    public Result audit(@RequestBody ExamineDo requestParam) {
        return reviewsService.audit(requestParam);
    }



}
