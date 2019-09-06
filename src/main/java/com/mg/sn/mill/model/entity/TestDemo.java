package com.mg.sn.mill.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper=false)
@EqualsAndHashCode(callSuper=false)
public class TestDemo {

    private boolean name;

    private boolean id;
}
