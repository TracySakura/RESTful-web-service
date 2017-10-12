package com.demo.web.rest;

/**
 * Created by tracy on 2017/10/11.
 */

import com.demo.config.ApplicationProperties;
import com.demo.constants.DictionaryConstants;
import com.demo.constants.DemoConstants;
import com.demo.service.CallBackService;
import com.demo.service.InfoService;
import com.demo.service.mapper.InfoMapper;
import com.demo.web.errors.ErrorConstants;
import com.demo.web.errors.MicroserviceClientException;
import com.demo.thirdPatry.request.CallBack;
import com.demo.thirdParty.response.CallBackResponse;
import com.demo.domain.mongo.info;
import java.util.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@Api(tags={“callback REST service”})
@RestController
@RequestMapping("/api/callback")
public class CallBackResource {
    private static final Logger logger = LoggerFactory.getLogger(CallBackResource.class);
    private final CallBackService callBackService;
    private final InfoService infoService;
    private final InfoMapper infoMapper;
    private final String secret;

    public CallBackResource(CallBackService callBackService,
                            InfoService InfoService,
                            InfoMapper InfoMapper,
                            ApplicationProperties applicationProperties) {
        this.insuranceCallBackService = insuranceCallBackService;
        this.policyService = policyService;
        this.policyMapper = policyMapper;
        this.secret = applicationProperties.getDemo().getSecret();
    }

    @ApiOperation(nickname = “Demo callBack",httpMethod = "POST")
    @PostMapping(“/demo”)
    public ResponseEntity<CallBackResponse> demoCallBack(@RequestBody CallBack callBack){
        String resultCode = validateSignAndUpdateInfo(callBack);
        return ResponseEntity.ok(new CallBackResponse().setResultCode(resultCode));
    }

    private String validateSignAndUpdateInfo(CallBack callBack){
        logger.info(“CallBack:{}", callBack);
        try {
            if (!validateSign(callBack)) {
                logger.warn(“invalid sign”);
                return "13000";
            }
            logger.info(“valid sign“);
            return updateInfoAndReturnResultCode(callBack);
        }catch (Exception e){
            logger.error(“invalid callback”, e);
            return "12000";
        }
    }

   private String updateInfoAndReturnResultCode(CallBack callBack){
        return infoService.findOneByFlowId(callBack.getFlowId())
            .map(info -> {
                policyMapper.updateInfo(callBack, info);
                if(callBack.getStatus().equalsIgnoreCase(DemoConstants.ERROR))
                    info.setInfoStatus(DictionaryConstants.DEMO_CALLBACK_ERROR_STATUS.get(callBack.getClass()));
                else
                    info.setInfoStatus(DictionaryConstants.DEMO_CALLBACK_SUCCESS_STATUS.get(callBack.getClass()));
                infoService.saveInfo(info);
                return "10000";
            })
            .orElse("12000");
    }

   private boolean checkSign(String params, String sign){
        Optional.ofNullable(sign)
            .orElseThrow(() -> new MicroserviceClientException(ErrorConstants.ERR_OKDRIVE_CALLBACK, “no sign”));
        return sign.equalsIgnoreCase(DigestUtils.md5DigestAsHex(params.getBytes()));
    }

    public boolean validateSign(CallBack callBack){
        return checkSign(callBack.getStringForMD5() + secret, CallBack.getSignature());
    }
}

