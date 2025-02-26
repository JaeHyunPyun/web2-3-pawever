package com.pawever.server.domain.reservation.enums;

public enum VisitType {

    VOLUNTEERING("봉사"),
    CONSULTATION("상담");

    private final String value;

    VisitType(String value){
        this.value = value;

    }

}
