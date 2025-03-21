package io.ihankun.framework.core.lunar.entity;

/**
 * 道历节日
 *
 * @author hankun
 */
public class TaoFestival {

  /**
   * 名称
   */
  private final String name;

  /**
   * 备注
   */
  private final String remark;

  public TaoFestival(String name, String remark) {
    this.name = name;
    this.remark = null == remark ? "" : remark;
  }

  public TaoFestival(String name) {
    this(name, null);
  }

  public String getName() {
    return name;
  }

  public String getRemark() {
    return remark;
  }

  @Override
  public String toString() {
    return name;
  }

  public String toFullString() {
    StringBuilder s = new StringBuilder();
    s.append(name);
    if (null != remark && remark.length() > 0) {
      s.append("[");
      s.append(remark);
      s.append("]");
    }
    return s.toString();
  }
}
