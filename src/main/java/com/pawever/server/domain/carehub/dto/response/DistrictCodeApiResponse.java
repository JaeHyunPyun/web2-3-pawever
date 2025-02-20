package com.pawever.server.domain.carehub.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class DistrictCodeApiResponse {
    private Response response;

    @Getter
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    public static class Header {
        private String reqNo;
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    public static class Body {
        private Items items;
    }

    @Getter
    public static class Items {
        private List<DistrictCodeDTO> item;
    }
}
