package bcs.com.polymap.model;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class Resp{
  private List<Alert> alert;
  public void setAlert(List<Alert> alert){
   this.alert=alert;
  }
  public List<Alert> getAlert(){
   return alert;
  }
}