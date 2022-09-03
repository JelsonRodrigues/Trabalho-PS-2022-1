package main.instructions;

import java.util.HashSet;
import main.Instruction;
import main.Registers;
import main.AddressingMode;

public class Call extends Instruction {
    public Call(){
        super("CALL", 15, 2, 1);
        HashSet<AddressingMode> modes = new HashSet<AddressingMode>();
        modes.add(AddressingMode.DIRECT);    
        modes.add(AddressingMode.INDIRECT);
        this.setAddressingModesSuported(modes);
    }
    
    @Override
    public void doOperation(Registers registers) {
        return;
    }

}