package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.LimeLight;
import org.firstinspires.ftc.teamcode.mechanisms.Turret;
import org.firstinspires.ftc.teamcode.mechanisms.ServoCam;

@TeleOp(name = "TeleOp Main Torreta", group = "TeleOp")
public class TeleOpMain extends OpMode {

    private Turret turret;
    private LimeLight limelight;
    private ServoCam servoCam;

    // Borda de subida (Rising Edge) para botões A, B, X e D-Pad
    private boolean lastA = false;
    private boolean lastB = false;
    private boolean lastX = false;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;

    private int currentPipeline = 0;

    // Velocidade de incremento do servo pelos gatilhos R2 / L2
    private final double SERVO_SPEED = 0.008;

    @Override
    public void init() {
        turret = new Turret();
        turret.init(hardwareMap);

        limelight = new LimeLight();
        limelight.init(hardwareMap);

        servoCam = new ServoCam();
        servoCam.init(hardwareMap);

        turret.resetTimer();

        telemetry.addLine("======================================");
        telemetry.addLine("A -> Pipeline 0 | B -> Pipeline 1 | X -> Pipeline 2");
        telemetry.addLine("R2 -> Mover Servo (+45°)");
        telemetry.addLine("L2 -> Mover Servo (-45°)");
        telemetry.addLine("======================================");
        telemetry.update();
    }

    @Override
    public void loop() {
        // --- 1. ALTERNÂNCIA DE PIPELINE (BOTÕES A, B, X) ---
        if (gamepad1.a && !lastA) {
            limelight.setPipeline(0);
            currentPipeline = 0;
        } else if (gamepad1.b && !lastB) {
            limelight.setPipeline(1);
            currentPipeline = 1;
        } else if (gamepad1.x && !lastX) {
            limelight.setPipeline(2);
            currentPipeline = 2;
        }

        lastA = gamepad1.a;
        lastB = gamepad1.b;
        lastX = gamepad1.x;

        // --- 2. CONTROLO DO SERVO DA CÂMERA (GATILHOS R2 / L2) ---
        if (gamepad1.right_trigger > 0.05) {
            servoCam.adjustPosition(gamepad1.right_trigger * SERVO_SPEED);
        } else if (gamepad1.left_trigger > 0.05) {
            servoCam.adjustPosition(-gamepad1.left_trigger * SERVO_SPEED);
        }

        // --- 3. RASTREAMENTO DINÂMICO DA TORRETA ---
        LLResult result = limelight.getLatestResult();
        turret.update(result); // A torreta roda automaticamente ao detetar alvos em movimento

        // --- 4. CALIBRAÇÃO EM TEMPO REAL DO kP (D-PAD CIMA / BAIXO) ---
        if (gamepad1.dpad_up && !lastDpadUp) {
            turret.setkP(turret.getkP() + 0.001);
        }
        if (gamepad1.dpad_down && !lastDpadDown) {
            turret.setkP(Math.max(0, turret.getkP() - 0.001));
        }
        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;

        // --- 5. TELEMETRIA ---
        telemetry.addData("Pipeline Ativa", "Pipeline %d", currentPipeline);
        telemetry.addData("Posição ServoCam", "%.2f", servoCam.getCurrentPosition());

        if (result != null && result.isValid()) {
            telemetry.addData("Status Torreta", "RASTREADO (tx: %.2f°)", result.getTx());
        } else {
            telemetry.addData("Status Torreta", "PARADA (Sem alvo no campo)");
        }

        telemetry.addData("kP Atual", "%.4f", turret.getkP());
        telemetry.addData("Ticks Encoder", turret.getCurrentPosition());

        telemetry.update();
    }
}


