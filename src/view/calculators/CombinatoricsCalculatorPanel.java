package view.calculators;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import utils.AbstractCalculator;
import view.components.CCDisplay;
import view.components.CCNumPad;
/**
 * 
 * MISSING JAVADOC.
 *
 */
public class CombinatoricsCalculatorPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static String opFormat = "";
    /**
     * 
     * @param controller
     */
    public CombinatoricsCalculatorPanel(final AbstractCalculator controller) {
        final var display = new CCDisplay();
        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.NORTH);
        controller.setDisplay(display);
        final ActionListener btnAl = e -> {
            final var btn = (JButton) e.getSource();
            controller.getManager().read(btn.getText());
            display.updateText(opFormat.isBlank() ? controller.getManager().getCurrentState().stream().reduce("", (a, b) -> a + b) : opFormat);
        };
        final ActionListener calculateAl = e -> {
            display.updateUpperText(controller.getManager().getCurrentState().stream().reduce("", (a, b) -> a + " " + b) + " =");
            controller.getManager().calculate();
            display.updateText(controller.getManager().getCurrentState().stream().reduce("", (a, b) -> a + b));
            opFormat = "";
        };
        final ActionListener backspaceAl = e -> {
            if (controller.getManager().getCurrentState().get(controller.getManager().getCurrentState().size() - 1).length() > 1) {
                opFormat = "";
            }
            controller.getManager().deleteLast();
            display.updateText(controller.getManager().getCurrentState().stream().reduce("", (a, b) -> a + b));
        };
        final var numpad = new CCNumPad(btnAl, calculateAl, backspaceAl);
        numpad.getButtons().get("(").setEnabled(false);
        numpad.getButtons().get(")").setEnabled(false);
        numpad.getButtons().get(".").setEnabled(false);
        this.add(numpad, BorderLayout.WEST);
        this.add(new OperationsPanel(controller, display), BorderLayout.CENTER);
        this.add(new ExplainationPanel(), BorderLayout.EAST);
    }
    static class OperationsPanel extends JPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final AbstractCalculator controller;
        private final CCDisplay display;

        OperationsPanel(final AbstractCalculator controller, final CCDisplay display) {
            this.display = display;
            this.controller = controller;
            this.setLayout(new GridLayout(8, 1));
            this.createButton("Sequences", "sequencesNumber");
            this.createButton("Factorial", "factorial");
            this.createButton("Binomial Coefficient", "binomialCoefficient");
            this.createButton("Scombussolamento", "scombussolamento");
            this.createButton("Partitions", "bellNumber");
            this.createButton("Partitions(binary)", "stirlingNumber");
            this.createButton("Fibonacci", "fibonacci");
            this.createButton("Fibonacci(binary)", "binaryFibonacci");
        }
        private void createButton(final String btnName, final String opName) {
            final var btn = new JButton(btnName);
            btn.addActionListener(e -> {
                final String closer = controller.isBinaryOperator(opName) ? " ," : " )";
                opFormat = btnName + "(" + controller.getManager().getCurrentState().stream().reduce("", (a, b) -> a + " " + b) + closer;
                display.updateText(opFormat);
                controller.getManager().read(opName);
            });
            this.add(btn);
        }
    }
    static class ExplainationPanel extends JPanel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        ExplainationPanel() {
            this.setLayout(new GridLayout(8, 1));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
            this.add(new JButton("?"));
        }
    }
}
