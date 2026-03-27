/**
 * 作者: Liu Jiaxu (B01051251)
 */

package com.xjzai1.xjzai1picturebackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

