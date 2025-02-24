package com.pawever.server.domain.carehub.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbandonedPetApiResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {

        @JsonProperty("reqNo")
        private Long reqNo;

        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {

        @JsonProperty("items")
        private Items items;

        @JsonProperty("numOfRows")
        private Integer numOfRows;

        @JsonProperty("pageNo")
        private Integer pageNo;

        @JsonProperty("totalCount")
        private Integer totalCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {

        @JsonProperty("item")
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("desertionNo")
        private String desertionNo;

        @JsonProperty("filename")
        private String filename;

        @JsonProperty("happenDt")
        private String happenDt;

        @JsonProperty("happenPlace")
        private String happenPlace;

        @JsonProperty("kindCd")
        private String kindCd;

        @JsonProperty("colorCd")
        private String colorCd;

        @JsonProperty("age")
        private String age;

        @JsonProperty("weight")
        private String weight;

        @JsonProperty("noticeNo")
        private String noticeNo;

        @JsonProperty("noticeSdt")
        private String noticeSdt;

        @JsonProperty("noticeEdt")
        private String noticeEdt;

        @JsonProperty("popfile")
        private String popfile;

        @JsonProperty("processState")
        private String processState;

        @JsonProperty("sexCd")
        private String sexCd;

        @JsonProperty("neuterYn")
        private String neuterYn;

        @JsonProperty("specialMark")
        private String specialMark;

        @JsonProperty("careNm")
        private String careNm;

        @JsonProperty("careTel")
        private String careTel;

        @JsonProperty("careAddr")
        private String careAddr;

        @JsonProperty("orgNm")
        private String orgNm;

        @JsonProperty("chargeNm")
        private String chargeNm;

        @JsonProperty("officetel")
        private String officetel;
    }
}
