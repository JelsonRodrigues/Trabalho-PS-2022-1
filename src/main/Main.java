// package main;

// import java.util.ArrayList;

// import javafx.application.*;
// import javafx.collections.ObservableList;
// import javafx.fxml.*;
// import javafx.stage.*;
// import javafx.scene.*;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;
// import main.errors.UndefinedExecutionMode;
// import main.gui.*;
// import main.instructions.Stop;

// public class Main extends Application {
//     private static VirtualMachine maquina_virtual = new VirtualMachine();
//     private static CPU cpu = maquina_virtual.getCPU();
//     private static ArrayList<Instruction> instructions = cpu.getInstructions();
//     private static Registers registers = cpu.getRegisters();
//     private static Memory memory = maquina_virtual.getMemory();
//     private Instruction stop_instruction = new Stop();
//     private Thread thread1 = new Thread();
//     private int memoryMaximumDisplay = 1000;

//     // User interaction Area
//     @FXML
//     Label current_mode;
//     @FXML
//     TextField output_area;
//     public String output_public;
//     @FXML
//     Button execute_instruction;
//     @FXML
//     Spinner<Integer> clock;

//     // Registers Area
//     @FXML
//     TextField PC_value;
//     @FXML
//     TextField SP_value;
//     @FXML
//     TextField ACC_value;
//     @FXML
//     TextField MOP_value;
//     @FXML
//     TextField RI_value;
//     @FXML
//     TextField RE_value;

//     // Instruction area
//     @FXML
//     TextField opcode_value;
//     @FXML
//     TextField mnemonic_value;
//     @FXML
//     TextField operand1_value;
//     @FXML
//     TextField operand2_value;
//     @FXML
//     TextField addressing_mode_operand1;
//     @FXML
//     TextField addressing_mode_operand2;

//     // Memory area
//     @FXML
//     TableView<MemoryCell> memory_table;
//     @FXML
//     TableColumn<MemoryCell, Integer> index_memory;
//     @FXML
//     TableColumn<MemoryCell, Short> value_memory;
//     @FXML
//     TableView<MemoryCell> stack_table;
//     @FXML
//     TableColumn<MemoryCell, Integer> index_stack;
//     @FXML
//     TableColumn<MemoryCell, Short> value_stack;

//     public static void main(String[] args) {
//         launch(args);
//     }

//     @Override
//     public void start(Stage stage) throws Exception {
//         Parent root = FXMLLoader.load(getClass().getResource("gui/GUI.fxml"));

//         Scene scene = new Scene(root);

//         stage.setTitle("Virtual Machine PS");
//         stage.setScene(scene);
//         stage.show();
//     }

//     @FXML
//     public void initialize() {
//         maquina_virtual.initMachine();
//         thread1 = new Thread();

//         try {
//             //maquina_virtual.readFile(System.getProperty("java.class.path").split(";")[0] + "/main/file.txt");
//             maquina_virtual.readFile("D:/Scripts/trabalho-ps-ufpel-2022-1/src/assembler/tests/test.OBJ");
//         } catch (Exception e) {
//             e.printStackTrace();
//             System.exit(0);
//         }

//         SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000);
//         clock.setValueFactory(valueFactory);

//         index_memory.setCellValueFactory(new PropertyValueFactory<MemoryCell, Integer>("address"));
//         value_memory.setCellValueFactory(new PropertyValueFactory<MemoryCell, Short>("value"));

//         for (int c = 0; c < memoryMaximumDisplay; c++) {
//             memory_table.getItems().add(new MemoryCell(c, (short) memory.getMemoryPosition(c)));
//         }
//         memory_table.scrollTo(maquina_virtual.defaultStackSize + maquina_virtual.defaultStackStartIndex);

//         index_stack.setCellValueFactory(new PropertyValueFactory<MemoryCell, Integer>("address"));
//         value_stack.setCellValueFactory(new PropertyValueFactory<MemoryCell, Short>("value"));

//         for (int c = maquina_virtual.defaultStackStartIndex; c < (maquina_virtual.defaultStackSize
//                 + maquina_virtual.defaultStackStartIndex); c++) {
//             stack_table.getItems().add(new MemoryCell(c, (short) memory.getMemoryPosition(c)));
//         }

//         setupUI();
//     }

//     @Override
//     public void stop() throws Exception {
//         super.stop();
//         exitApplication();
//     }

//     public void setupUI() {
//         updateRegisters();
//         updateStack();
//         updateMemory();
//         updateCurrentInstruction();
//         changeMode();
//     }

//     /* Controller methods */
//     public void changeMode() {
//         try {
//             if (current_mode.getText().equals(ExecutionMode.CONTINUOUS.toString())) {
//                 current_mode.setText(ExecutionMode.STEP.toString());
//                 registers.setMOP((byte) ExecutionMode.getNumber(ExecutionMode.STEP));
//                 clock.setDisable(true);
//             } else {
//                 current_mode.setText(ExecutionMode.CONTINUOUS.toString());
//                 registers.setMOP((byte) ExecutionMode.getNumber(ExecutionMode.CONTINUOUS));
//                 clock.setDisable(false);
//             }
//         } catch (UndefinedExecutionMode e) {
//             e.printStackTrace();
//             exitApplication();
//         }
//         updateRegisters();
//         updateExecuteInstructionButton();
//     }

//     public void updateExecuteInstructionButton() {
//         try {
//             if (registers.getMOP() == ExecutionMode.getNumber(ExecutionMode.STEP)) {
//                 execute_instruction.setText("Execute Next Instruction");
//             } else {
//                 execute_instruction.setText("Execute All Instructions");
//             }
//         } catch (UndefinedExecutionMode e) {
//             e.printStackTrace();
//             exitApplication();
//         }
//     }

//     public void exitApplication() {
//         Platform.exit();
//         System.exit(0);
//     }

//     public void updateRegisters() {
//         PC_value.setText(Short.toString(registers.getPC()));
//         SP_value.setText(Short.toString(registers.getSP()));
//         ACC_value.setText(Short.toString(registers.getACC()));
//         MOP_value.setText(Short.toString(registers.getMOP()));
//         RI_value.setText(Short.toString(registers.getRI()));
//         RE_value.setText(Short.toString(registers.getRE()));
//     }

//     public void clearOutput() {
//         output_area.setText("");
//     }

//     public void executeInstruction() {
//         thread1 = new Thread(new Runnable() {
//             @Override
//             public void run() {
//                 try {
//                     switch (ExecutionMode.getExecutionMode(registers.getMOP())) {
//                         case STEP:
//                             maquina_virtual.cycle();
//                             updateCurrentInstruction();
//                             updateRegisters();
//                             updateMemory();
//                             updateStack();
//                             updateOutput();
//                             break;
//                         case CONTINUOUS:
//                             while (ExecutionMode.getExecutionMode(registers.getMOP()) == ExecutionMode.CONTINUOUS) {
//                                 maquina_virtual.cycle();
//                                 updateCurrentInstruction();
//                                 updateRegisters();
//                                 updateMemory();
//                                 updateStack();
//                                 updateOutput();
//                                 try {
//                                     Thread.sleep((long) (1000 / clock.getValue()));
//                                 } catch (Exception o) {
//                                     o.printStackTrace();
//                                     break;
//                                 }
//                                 if (registers.getRI() == stop_instruction.getOpcode()){
//                                     Thread.currentThread().interrupt();
//                                     break;
//                                 }
//                             }
//                             break;
//                     }
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                     exitApplication();
//                 }
//             }
//         });
//         thread1.start();
//     }

//     public void updateStack() {
//         // stack_table.scrollTo(registers.getSP() -
//         // maquina_virtual.defaultStackStartIndex);
//         stack_table.getSelectionModel().clearAndSelect(registers.getSP() - maquina_virtual.defaultStackStartIndex);
//         ObservableList<MemoryCell> tableMemory = stack_table.getItems();

//         for (int c = 0; c < tableMemory.size(); c++) {
//             tableMemory.get(c).setValue(memory.getMemoryPosition(c + maquina_virtual.defaultStackStartIndex));
//         }
//         stack_table.setItems(tableMemory);
//         stack_table.refresh();
//     }

//     public void updateMemory() {
//         // memory_table.scrollTo(registers.getPC());
//         memory_table.getSelectionModel().clearAndSelect(registers.getPC());
//         ObservableList<MemoryCell> tableMemory = memory_table.getItems();

//         for (int c = 0; c < tableMemory.size(); c++) {
//             tableMemory.get(c).setValue(memory.getMemoryPosition(c));
//         }
//         memory_table.setItems(tableMemory);
//         memory_table.refresh();
//     }

//     public void updateMainMemory() {
//         ObservableList<MemoryCell> tableMemory = memory_table.getItems();

//         for (int c = 0; c < tableMemory.size(); c++) {
//             memory.setMemoryPosition(tableMemory.get(c).getAddress(), tableMemory.get(c).getValue());
//         }
//     }

//     public void updateCurrentInstruction() {
//         int index_instruction = cpu.getCurrentInstructionIndex();
//         if (index_instruction >= 0 && index_instruction < instructions.size()) {
//             Instruction current_instruction = instructions.get(index_instruction);

//             opcode_value.setText(Short.toString(registers.getRI()));
//             mnemonic_value.setText(current_instruction.getMnemonic());

//             if (current_instruction instanceof OneOperandInstruction) {
//                 operand1_value.setText(Short.toString(((OneOperandInstruction) current_instruction).getOperand1()));
//                 addressing_mode_operand1.setText(
//                         ((OneOperandInstruction) current_instruction).getCurrentOperand1AddressingMode().toString());
//             } else {
//                 operand1_value.setText("");
//                 addressing_mode_operand1.setText("");
//             }
//             if (current_instruction instanceof TwoOperandInstruction) {
//                 operand2_value.setText(Short.toString(((TwoOperandInstruction) current_instruction).getOperand2()));
//                 addressing_mode_operand2.setText(
//                         ((TwoOperandInstruction) current_instruction).getCurrentOperand2AddressingMode().toString());
//             } else {
//                 operand2_value.setText("");
//                 addressing_mode_operand2.setText("");
//             }
//         } else {
//             opcode_value.setText("");
//             mnemonic_value.setText("");
//             operand1_value.setText("");
//             addressing_mode_operand1.setText("");
//             operand2_value.setText("");
//             addressing_mode_operand2.setText("");
//         }
//     }

//     public void updateOutput() {
//         output_area.setText("");
//         for (int c = 950; c < memoryMaximumDisplay; c++) {
//             if (memory.getMemoryPosition(c) != 0) {
//                 output_area.setText(output_area.getText() + memory.getMemoryPosition(c));
//             }
//         }
//     }

//     public void updatePC() {
//         registers.setPC(Short.parseShort(PC_value.getText()));
//         updateMemory();
//     }

//     public void updateSP() {
//         registers.setSP(Short.parseShort(SP_value.getText()));
//         updateStack();
//     }

//     public void updateACC() {
//         registers.setACC(Short.parseShort(ACC_value.getText()));
//     }

//     public void updateRI() {
//         registers.setRI(Short.parseShort(RI_value.getText()));
//     }

//     public void updateRE() {
//         registers.setRE(Short.parseShort(RE_value.getText()));
//     }

//     public void assembleFile(){
//         // Exibe uma janela de seleção de arquivos
//         // Chama o método Assembler.assemble para cada arquivo selecionado
//         // Caso ocorra erro, mostra numa janelinha que ocorreram erros
//         // Caso contrário mostra uma janelinha de ok
//     }
// }
