package main.instructions;

import java.util.HashSet;
import main.AddressingMode;
import main.Instruction;
import main.Registers;

public class Br extends Instruction {
    public Br(){
        super("BR", 0, 2, 1);
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
