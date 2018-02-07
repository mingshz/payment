package me.jiangcai.demo.pay.bean;

import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.PublicAccount;

/**
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public class DebugPublicAccount extends PublicAccount {

    private PublicAccountSupplier supplier;

    public DebugPublicAccount() {
        this("http://192.168.1.52:8009/");
    }

    public DebugPublicAccount(String url) {
        setAppID("wx198ba167229080c1");
        setAppSecret("e3b05e7dc329c53dc781469ec1784792");
        setMchID("1498570872");
        setApiKey("1sapeoif78366vbcxbzjs8ew8182838d");
        setInterfaceURL(url);
        setInterfaceToken("jiangcai");
    }

    @Override
    public PublicAccountSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(PublicAccountSupplier supplier) {
        this.supplier = supplier;
    }
}
