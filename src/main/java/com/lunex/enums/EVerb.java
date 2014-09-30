package com.lunex.enums;


public enum EVerb{
  
  GET("get"), POST("post"), DELETE("delete"), ALL("*");
  private String verd;

  private EVerb(String stringVal) {
    verd = stringVal;
  }

  public String toString() {
    return verd;
  }

  public static EVerb getEVerd(String verd) {
    for (EVerb e : EVerb.values()) {
      if (verd.equalsIgnoreCase(e.verd))
        return e;
    }
    return null;
  }
}