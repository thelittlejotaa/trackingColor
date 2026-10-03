package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class ServoCam {
    private Servo tiltServo;

    // --- LIMITES FÍSICOS DO SERVO (45 GRAUS PARA CADA LADO) ---
    // Num servo padrão de 180°:
    // 0.50 = Centro (0°)
    // 0.75 = +45° (Limite Máximo)
    // 0.25 = -45° (Limite Mínimo)
    public static final double POS_MAX = 0.65;
    public static final double POS_MIN = 0.35;
    public static final double POS_CENTER = 0.50;

    private double currentPosition = POS_CENTER;

    public void init(HardwareMap hwmap) {
        tiltServo = hwmap.get(Servo.class, "tiltServo");
        if (tiltServo != null) {
            tiltServo.setPosition(currentPosition);
        }
    }

    /**
     * Ajusta a posição do servo gradualmente respeitando os limites de 45°.
     * @param delta Valor a somar ou subtrair à posição atual
     */
    public void adjustPosition(double delta) {
        currentPosition += delta;
        currentPosition = Range.clip(currentPosition, POS_MIN, POS_MAX);

        if (tiltServo != null) {
            tiltServo.setPosition(currentPosition);
        }
    }

    public double getCurrentPosition() {
        return currentPosition;
    }
}