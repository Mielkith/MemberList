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
    
    SmallStruct()
    {
        present = false;
        typo = false;
    }

    public SmallStruct(int x, boolean isFound)
    {
       lineNumber = x;
       present = isFound;
       typo = false;
    }
    
     public SmallStruct(int x, boolean isFound, boolean typo)
    {
       lineNumber = x;
       present = isFound;
       this.typo = typo;
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
    
    
}
