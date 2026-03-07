/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author trisha
 */
public interface IEmployee {
    public interface EmployeeInterface {
    String getId();  
    String getFirstName();
    String getLastName();
    String getStatus();
    String getBirthDate();
    
 
    // --- Contact & IDs ---
    String getAddress();
    String getPhone();
    String getSssNumber();
    String getPhilHealthNumber();
    String getTinNumber();
   

    String getPagIbigNumber();
   
    // --- Employment info ---
     String getstatus();
     String getPosition();
     String getSupervisor() ;

 

}
}
