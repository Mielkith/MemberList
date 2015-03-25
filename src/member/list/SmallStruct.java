/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package member.list;

/**
 *
 * @author Weary
 */
public class SmallStruct {
    private Boolean present;
    private int lineNumber;
    private Boolean typo;
    private String DOB;
    private String studentNum;
    private String name;
    
    
    SmallStruct()
    {
        present = false;
        typo = false;
    }

    public SmallStruct(int x, boolean isFound, String dob, String studentNum)
    {
       lineNumber = x;
       present = isFound;
       typo = false;
        this.DOB = dob;
       this.studentNum = studentNum;
    }
    
     public SmallStruct(int x, boolean isFound, boolean typo, String dob, String studentNum)
    {
       lineNumber = x;
       present = isFound;
       this.typo = typo;
       this.DOB = dob;
       this.studentNum = studentNum;
    }
    
    
    public Boolean IsFound()
    {
        return present;
    }
    
    public int GetLineNumber()
    {
        return lineNumber;
    }
    
    public Boolean IsTypos()
    {        
        return typo;
    }
    
    public void SetDOB(String dob)
    {
        this.DOB = dob;
    }
    
      public void SetStudentNum(String studentNum)
    {
        this.studentNum = studentNum;
    }
      
         public void SetName(String name)
    {   
        this.name = name;
    }
    
         public String ToString()
         {
             StringBuilder sb = new StringBuilder();
             sb.append(this.name);
             sb.append(",");
             sb.append(this.studentNum);
             sb.append(",");
             sb.append(this.DOB);
             sb.append("\n");
             return sb.toString();
         }
    
}
