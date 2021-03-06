package chip8;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class CPU {

    @Getter @Setter
    private Memory memory;
    @Getter @Setter
    private Keyboard keyboard;
    @Getter @Setter
    private Screen screen;

    private boolean changeOnScreen = false;

    private Random RNG = new Random();

    public boolean wasChangeOnScreen() {
        return changeOnScreen;
    }

    public CPU() {
        this(new Memory(), new Keyboard(), new  Screen());
    }

    public CPU(Memory memory, Keyboard keyboard, Screen screen) {
        this.memory = memory;
        this.keyboard = keyboard;
        this.screen = screen;
    }

    public char fetchOpcode() {
        int a = memory.RAM[memory.PC] & 0xFF;
        int b = memory.RAM[memory.PC + 1] & 0xFF;
        char result = (char)((a << 8) | b);

        return result;
    }

    public void timersTick() {
        if (memory.soundTimer > 0)
            memory.soundTimer -= 1;
        if (memory.delayTimer > 0)
            memory.delayTimer -= 1;
    }

    public void tick() throws IllegalStateException {
        char opcode = fetchOpcode();

        switch (opcode & 0xF000) {
            case 0x0000:
                if ((opcode & 0x00FF) == 0x00E0) {
                    opcode00E0();
                    break;
                }
                else if ((opcode & 0x00FF) == 0x00EE) {
                    opcode00EE();
                    break;
                }
                else {
                    memory.PC += 2;
                    break;
                }

            case 0x1000:
                opcode1NNN();
                break;

            case 0x2000:
                opcode2NNN();
                break;

            case 0x3000:
                opcode3XNN();
                break;

            case 0x4000:
                opcode4XNN();
                break;

            case 0x5000:
                opcode5XY0();
                break;

            case 0x6000:
                opcode6XNN();
                break;

            case 0x7000:
                opcode7XNN();
                break;

            case 0x8000:
                switch (opcode & 0xF00F) {

                    case 0x8000:
                        opcode8XY0();
                        break;

                    case 0x8001:
                        opcode8XY1();
                        break;

                    case 0x8002:
                        opcode8XY2();
                        break;

                    case 0x8003:
                        opcode8XY3();
                        break;

                    case 0x8004:
                        opcode8XY4();
                        break;

                    case 0x8005:
                        opcode8XY5();
                        break;

                    case 0x8006:
                        opcode8XY6();
                        break;

                    case 0x8007:
                        opcode8XY7();
                        break;

                    case 0x800E:
                        opcode8XYE();
                        break;

                    default:
                        throw new IllegalStateException("WRONG OPCODE: " + (int) opcode);
                }
                break;

            case 0x9000:
                opcode9XY0();
                break;

            case 0xA000:
                opcodeANNN();
                break;

            case 0xB000:
                opcodeBNNN();
                break;

            case 0xC000:
                opcodeCXNN();
                break;

            case 0xD000:
                opcodeDXYN();
                break;

            case 0xE000:
                if ((opcode & 0xF0FF) == 0xE09E) {
                    opcodeEX9E();
                    break;
                }
                else if ((opcode & 0xF0FF) == 0xE0A1) {
                    opcodeEXA1();
                    break;
                }
                else {
                    throw new IllegalStateException("WRONG OPCODE: " + (int) opcode);
                }

            case 0xF000:
                switch (opcode & 0xF0FF) {
                    case 0xF007:
                        opcodeFX07();
                        break;

                    case 0xF00A:
                        opcodeFX0A();
                        break;

                    case 0xF015:
                        opcodeFX15();
                        break;

                    case 0xF018:
                        opcodeFX18();
                        break;

                    case 0xF01E:
                        opcodeFX1E();
                        break;

                    case 0xF029:
                        opcodeFX29();
                        break;

                    case 0xF033:
                        opcodeFX33();
                        break;

                    case 0xF055:
                        opcodeFX55();
                        break;

                    case 0xF065:
                        opcodeFX65();
                        break;

                    default:
                        throw new IllegalStateException("WRONG OPCODE: " + (int) opcode);
                }
                break;

            default:
                throw new IllegalStateException("WRONG OPCODE: " + (int) opcode);
        }

    }

    /***
     * Clears the screen.
     */
    public void opcode00E0() {
        screen.clear();
        changeOnScreen = true;

        memory.PC += 2;
    }

    /***
     *  Returns from a subroutine.
     */
    public void opcode00EE() {
        memory.PC = memory.stack[memory.SP];
        --memory.SP;
    }
    /***
     *  Jumps to address NNN.
     */
    public void opcode1NNN() {
        char newAddress = (char) (fetchOpcode() & 0x0FFF);
        memory.PC = newAddress;
    }

    /***
     *  Calls subroutine at NNN.
     */
    public void opcode2NNN() {
        ++memory.SP;
        memory.stack[memory.SP] = (char) (memory.PC + 2);

        char newAddress = (char)(fetchOpcode() & 0x0FFF);
        memory.PC = newAddress;
    }

    /***
     *  Skips the next instruction if VX equals NN.
     *  (Usually the next instruction is a jump to skip a code block)
     */
    public void opcode3XNN() {
        int NN = (byte)(fetchOpcode() & 0xFF);
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        if (memory.V[X] == NN)
            memory.PC += 4;
        else
            memory.PC += 2;
    }

    /***
     *  Skips the next instruction if VX doesn't equal NN.
     *  (Usually the next instruction is a jump to skip a code block)
     */
    public void opcode4XNN() {
        int NN = (byte) (fetchOpcode() & 0xFF);
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        if (memory.V[X] != NN)
            memory.PC += 4;
        else
            memory.PC += 2;
    }

    /***
     *  Skips the next instruction if VX equals VY.
     *  (Usually the next instruction is a jump to skip a code block)
     */
    public void opcode5XY0() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        if (memory.V[X] == memory.V[Y])
            memory.PC += 4;
        else
            memory.PC += 2;
    }

    /***
     *  Sets VX to NN.
     */
    public void opcode6XNN() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int NN = (fetchOpcode() & 0x00FF);

        memory.V[X] = (byte) NN;
        memory.PC += 2;
    }

    /***
     *  Adds NN to VX. (Carry flag is not changed)
     */
    public void opcode7XNN() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int NN = (fetchOpcode() & 0x00FF);

        memory.V[X] += NN;
        memory.PC += 2;
    }

    /***
     *   Sets VX to the value of VY.
     */
    public void opcode8XY0() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        memory.V[X] = memory.V[Y];
        memory.PC += 2;
    }

    /***
     *   Sets VX to VX or VY.
     *   (Bitwise OR operation)
     */
    public void opcode8XY1() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        memory.V[X] = (byte)((memory.V[X] & 0xFF) | (memory.V[Y] & 0xFF));
        memory.PC += 2;
    }

    /***
     *   Sets VX to VX and VY.
     *   (Bitwise AND operation)
     */
    public void opcode8XY2() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        memory.V[X] = (byte)((memory.V[X] & 0xFF) & (memory.V[Y] & 0xFF));
        memory.PC += 2;
    }

    /***
     *   Sets VX to VX xor VY.
     */
    public void opcode8XY3() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        memory.V[X] = (byte)((memory.V[X] & 0xFF) ^ (memory.V[Y] & 0xFF));
        memory.PC += 2;
    }

    /***
     *   Adds VY to VX.
     *   VF is set to 1 when there's a carry, and to 0 when there isn't.
     */
    public void opcode8XY4() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;
        int result = (int) (memory.V[X] & 0xFF) + (memory.V[Y] & 0xFF);

        if (result >= 0xFF) //  overflow
            memory.V[0xF] = 0x1;
        else
            memory.V[0xF] = 0x0;

        memory.V[X] = (byte) ((memory.V[X] & 0xFF) + (memory.V[Y] & 0xFF));
        memory.PC += 2;
    }

    /***
     *   VY is subtracted from VX.
     *   VF is set to 0 when there's a borrow, and 1 when there isn't.
     */
    public void opcode8XY5() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        if ((memory.V[X] & 0xFF) > (memory.V[Y] & 0xFF)) // not borrow
            memory.V[0xF] = 0x1;
        else
            memory.V[0xF] = 0x0;

        memory.V[X] = (byte)((memory.V[X] & 0xFF) - (memory.V[Y] & 0xFF));
        memory.PC += 2;
    }

    /***
     *   Stores the least significant bit
     *   of VX in VF and then shifts VX to the right by 1.
     */
    public void opcode8XY6() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        memory.V[0xF] = (byte)(memory.V[X] & 0x01);

        memory.V[X] = (byte)((memory.V[X] & 0xFF) >>> 1);
        memory.PC += 2;
    }

    /***
     *   Sets VX to VY minus VX. VF is set to 0
     *   when there's a borrow, and 1 when there isn't..
     */
    public void opcode8XY7() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        if ((memory.V[Y] & 0xFF) > (memory.V[X] & 0xFF)) // not borrow
            memory.V[0xF] = 0x1;
        else
            memory.V[0xF] = 0x0;

        memory.V[X] = (byte)((memory.V[Y] & 0xFF) - (memory.V[X] & 0xFF));
        memory.PC += 2;

    }

    /***
     *   Stores the most significant bit of VX
     *   in VF and then shifts VX to the left by 1
     */
    public void opcode8XYE() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        memory.V[0xF] = (byte)((memory.V[X] & 0b10000000) >>> 7);

        memory.V[X] = (byte)((memory.V[X] & 0xFF) << 1);
        memory.PC += 2;
    }

    /***
     *   Skips the next instruction if VX doesn't equal VY.
     *   (Usually the next instruction is a jump to skip a code block)
     */
    public void opcode9XY0() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;

        int VX = memory.V[X];
        int VY = memory.V[Y];

        if (VX != VY)
            memory.PC += 4;
        else
            memory.PC += 2;
    }

    /***
     *   Sets I to the address NNN.
     */
    public void opcodeANNN() {
        int NNN = (fetchOpcode() & 0x0FFF);

        memory.I = (char) NNN;
        memory.PC += 2;
    }

    /***
     *   Jumps to the address NNN plus V0.
     */
    public void opcodeBNNN() {
        int NNN = (fetchOpcode() & 0x0FFF);

        memory.PC = (char) (NNN + (memory.V[0] & 0xFF) );
    }

    /**
     * Sets VX to the result of a bitwise and operation
     * on a random number (Typically: 0 to 255) and NN.
     */
    public void opcodeCXNN() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int NN = (fetchOpcode() & 0x00FF);
        int randomByte = RNG.nextInt() & 0xFF;

        byte result = (byte) (NN & randomByte);

        memory.V[X] = result;
        memory.PC += 2;
    }

    /**
     * Draws a sprite at coordinate (VX, VY) that has a width
     * of 8 pixels and a height of N pixels. Each row of 8 pixels
     * is read as bit-coded starting from memory location I; I value
     * doesn’t change after the execution of this instruction. As described
     * above, VF is set to 1 if any screen pixels are flipped from set to unset
     * when the sprite is drawn, and to 0 if that doesn’t happen
     */
    public void opcodeDXYN() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int Y = (fetchOpcode() & 0x00F0) >>> 4;
        int N = (fetchOpcode() & 0x000F);
        int I = memory.I;

        int VX = memory.V[X] & 0xFF;
        int VY = memory.V[Y] & 0xFF;

        memory.V[0xF] = 0x0;

        for (int y = 0; y < N; ++y) {
            byte byteOfPixels = memory.RAM[I + y];

            for (int x = 0; x < 8; ++x) {
                boolean pixel;
                if ((byteOfPixels & (0x80 >>> x)) != 0)
                    pixel = true;
                else
                    pixel = false;

                if (pixel == true) {
                    int cordX = (x + VX) % Screen.WIDTH;
                    int cordY = (y + VY) % Screen.HEIGHT;

                    if (screen.flipPixel(cordX, cordY) == false)
                        memory.V[0xF] = 0x1;
                }
            }
        }
        memory.PC += 2;
        changeOnScreen = true;
    }

    /**
     * Skips the next instruction if the key stored in VX is pressed.
     * (Usually the next instruction is a jump to skip a code block)
     */
    public void opcodeEX9E() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        byte VX = memory.V[X];

        if (keyboard.isPressed(VX))
            memory.PC += 4;
        else
            memory.PC += 2;
    }

    /**
     * Skips the next instruction if the key stored in VX isn't pressed.
     * (Usually the next instruction is a jump to skip a code block)
     */
    public void opcodeEXA1() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        byte VX = memory.V[X];

        if (keyboard.isPressed(VX))
            memory.PC += 2;
        else
            memory.PC += 4;
    }

    /**
     * Sets VX to the value of the delay timer.
     */
    public void opcodeFX07() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        memory.V[X] = (byte) (memory.delayTimer & 0xFF);
        memory.PC += 2;
    }

    /**
     * A key press is awaited, and then stored in VX.
     */
    public void opcodeFX0A() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        byte theNumberOfKey = -1;
        for (byte i = 0; i < Keyboard.NUMBER_OF_KEYS; ++i) {
            if (keyboard.isPressed(i)) {
                theNumberOfKey = i;
                break;
            }
        }

        if (theNumberOfKey != -1) {
            memory.V[X] = theNumberOfKey;
            memory.PC += 2;
        }
    }

    /**
     * Sets the delay timer to VX.
     */
    public void opcodeFX15() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        memory.delayTimer = (char)(memory.V[X] & 0xFF);
        memory.PC += 2;
    }

    /**
     * Sets the sound timer to VX.
     */
    public void opcodeFX18() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;

        memory.soundTimer = (char)(memory.V[X] & 0xFF);
        memory.PC += 2;
    }

    /**
     * Adds VX to I.
     */
    public void opcodeFX1E() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int VX = (memory.V[X] & 0xFF);

        memory.I += VX;
        memory.PC += 2;
    }

    /**
     * Sets I to the location of the sprite for the character in VX.
     * Characters 0-F (in hexadecimal) are represented by a 4x5 font.
     */
    public void opcodeFX29() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int VX = (memory.V[X] & 0xFF);

        memory.I = (char) (5 * VX);
        memory.PC += 2;
    }

    /**
     * Stores the binary-coded decimal representation of VX, with the most
     * significant of three digits at the address in I, the middle digit
     * at I plus 1, and the least significant digit at I plus 2.
     * (In other words, take the decimal representation of VX, place the
     * hundreds digit in memory at location in I, the tens digit at
     * location I+1, and the ones digit at location I+2.)
     */
    public void opcodeFX33() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int VX = (memory.V[X] & 0xFF);
        int I = memory.I;

        memory.RAM[I] = (byte) (VX / 100);
        memory.RAM[I + 1] = (byte) ((VX / 10) % 10);
        memory.RAM[I + 2] = (byte) (VX % 10);

        memory.PC += 2;
    }

    /**
     * Stores V0 to VX (including VX) in memory starting at address I.
     * The offset from I is increased by 1 for each value written,
     * but I itself is left unmodified.
     */
    public void opcodeFX55() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int I = memory.I;

        System.arraycopy(memory.V, 0, memory.RAM, I, X + 1);
        memory.PC += 2;
    }

    /**
     * Fills V0 to VX (including VX) with values from memory starting at
     * address I. The offset from I is increased by 1 for each value written,
     * but I itself is left unmodified.
     */
    public void opcodeFX65() {
        int X = (fetchOpcode() & 0x0F00) >>> 8;
        int I = memory.I;

        System.arraycopy(memory.RAM, I, memory.V,0, X + 1);
        memory.PC += 2;
    }

    public String toString() {
        String jsonStr = new Gson().toJson(this);
        return jsonStr;
    }

    public CPU(String json) {
        Gson gson = new Gson();
        CPU cpuFromJson = gson.fromJson(json, CPU.class);

        memory = cpuFromJson.memory;
        keyboard = cpuFromJson.keyboard;
        screen = cpuFromJson.screen;
        changeOnScreen = cpuFromJson.changeOnScreen;
        RNG = cpuFromJson.RNG;
    }

    public static void main(String []args) {
        CPU cpu = new CPU(new Memory(), new Keyboard(), new Screen());

        System.out.println(cpu.toString());

        CPU newCpu = new CPU(cpu.toString());
        System.out.println(newCpu.toString());
    }

}
