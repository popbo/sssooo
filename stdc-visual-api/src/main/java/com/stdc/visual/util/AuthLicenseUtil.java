package com.stdc.visual.util;

import Aladdin.Hasp;
import Aladdin.HaspStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: wang_jie
 * @data: 2024/3/8--15:19
 * @describe:
 */
@Slf4j
public class AuthLicenseUtil {

    private static final Long feature = 5L;

    private static final String VENDOR_CODE  = "U9DRYRaJ/ZhZvDPoUiDIfcRoW7KgbgmbHOdE2z/UE9g0Y9x2cXD0VwbAgy1PRXHZbWqB5v/tFO0iRZ+c" +
            "w/URhFs2aTKW2QBtz+qcHF4b4HXjegNr3xrqAoiYjKBwG2qQsycRyjz+lPIU0hlC7S7akwJ7JjQBUnMc" +
            "1eixhkDE2O8wTjxgIYMd1i6E+89X6Wyl2s3yHvDfwiSboG9VSGll0HjEtlVokJKqt0okEZhld8bAHs1v" +
            "u0+CKOWPVduBdyjyM0WKg7QxXC0ADOcn/rTgF4ux7fAp9QogClukerDPFGef3293eztxjp1o5ADK3DKk" +
            "eqr8XHrqw4rWAfvRLIbCwxoIDUl+9XqzLeI1blsuYyI/JF3V/kdj47OclP+rTv0ExCvvgVZDgjzFit72" +
            "n8e5Q8CxT7hkhHLXxQGyvMwfU9CvbOZYgMN0dRfURGg8wf8RrUPugJCH3K7zUFYfvOEubJPLFWf+cUHf" +
            "CqUz0XAHPnr31ooegNXgnr9ci0/FzHzbnlIzafTRRem227TjC0Fu3hnH5JlXabFcPBoRpPHRW8kzLT+8" +
            "g96kpRr/PQ8XI0mC7iGdL4glt0/8uK5h1gf8RtVLe0uE5nTO2AJX9112L0TsEZlJIk4u+I++e25LssQ7" +
            "S7ztXqWt5Rrhxy55cQXBQlxRCyFVPtYq+iIGaAuGalKmE4ZqrvyX6y/KMaLN16VehfYVvRJ8zASnFnJo" +
            "e9Sr26pD5WLzuQvj1NdirsmQNx8401dG+m31dhPboPWRy8OR08E+XcNzerMs9Wbcj9Und+cBh8Fj8yk8" +
            "rAnOCQrI5HV6YN+7b1FZEPr8/pJeMe391fhxxMxIXo0EG2Zd1CAYOxOYoQv5cSPYYQdhs2QurVV0LB9+" +
            "Wg6pmFWlXlQWA+stiIrsbmuxv4gzJNAksxWVikC1haRjkyD1LSXh027bIjOkUTekcxU5CzlerKCPBCdM" +
            "s0xBEUPQtuSEEVB24HOZ1g==";

    /**
     * 监测license的状态
     */
    public static synchronized void checkAuthLicenseStatus(ConfigurableApplicationContext applicationContext){
        Hasp hasp = new Hasp(feature);
        hasp.login(VENDOR_CODE);
        int status = hasp.getLastError();
        hasp.logout();
        if (HaspStatus.HASP_STATUS_OK != status) {
            String msg = "license expired! status:"+status;
            //输入响应内容
            log.error(msg);
            applicationContext.close();
            System.exit(-1);
            return;
        }
        //放行
        log.info("license is ok! status:"+status);
    }
}
