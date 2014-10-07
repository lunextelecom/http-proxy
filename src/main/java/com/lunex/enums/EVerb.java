package com.lunex.enums;


// TODO: Auto-generated Javadoc
/**
 * The Enum EVerb.
 */
public enum EVerb{
  
  
  GET("get"), /** GET */
 POST("post"), /** POST */
 DELETE("delete"), /** DELETE. */
 HEAD("head"), /** HEAD. */
 PUT("put"), /** PUT. */
 ALL("*");/** all*/
  
  /** The verd. */
  private String verd;

  /**
   * The Constructor.
   *
   * @param stringVal the string val
   */
  private EVerb(String stringVal) {
    verd = stringVal;
  }

  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  public String toString() {
    return verd;
  }

  /**
   * Gets the e verd.
   *
   * @param verd the verd
   * @return the e verd
   */
  public static EVerb getEVerd(String verd) {
    for (EVerb e : EVerb.values()) {
      if (verd.equalsIgnoreCase(e.verd))
        return e;
    }
    return null;
  }
}