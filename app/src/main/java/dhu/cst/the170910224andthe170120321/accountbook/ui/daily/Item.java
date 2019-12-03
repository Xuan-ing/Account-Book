package dhu.cst.the170910224andthe170120321.accountbook.ui.daily;

import java.math.BigDecimal;

public class Item {
    private String time;
    private String description;
    private BigDecimal money;
    private String type;
    private boolean operation;

    Item() {
        // empty
    }

    Item(String type, BigDecimal money, String description, boolean operation, String time) {
        this.description = description;
        this.money = money;
        this.type = type;
        this.operation = operation;
        this.time = time;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getMoney() {
        return money;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean getOperation() {
        return operation;
    }
    public void setOperation(boolean operation) {
        this.operation = operation;
    }

}
