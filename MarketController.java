package com.demo.web.controller;

/**
 * Created by tracy on 2017/10/11.
 */

import org.springframework.http.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import com.demo.service.ProductService;
import com.demo.web.vm.Market.BannerVM;
import com.demo.web.vm.Market.HomePageVM;

@Api(tags={"MarkertController"})
@RestController
@RequestMapping("/api/market”)
public class DemoController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    public MarkertController(ProductService productService){
        this.productService = productService;
    }

    @ApiOperation(nickname = "getHomePage",httpMethod = "GET")
    @GetMapping("/getHomePage")
    public ResponseEntity<HomePageVM> getHomePage(){
        HomePageVM homePageVM = new HomePageVM();
        List<BannerVM> bannerList= new ArrayList<>();
        bannerList.add(new BannerVM()
            .setTitle(“top service“)
            .setImg("https://####.jpg")
            .setUrl("https://######”);
        homePageVM.setBannerList(bannerList);
        return ResponseEntity.ok(homePageVM);
    }

}
