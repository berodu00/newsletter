package com.kz.magazine.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContentHashtagId implements Serializable {
    private Long content;
    private Long hashtag;
}
