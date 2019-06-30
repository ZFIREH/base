package cn.com.zhouhuo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouhuo
 * @date 2019/6/29 19:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Trader trader;
    private int year;
    private int value;
}
