package gui.dashboard;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import models.Consultation;
import models.Message;
import models.Patient;
import models.Doctor;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel that lets a patient view consultations with their doctor and send messages.
 */
public class PatientConsultationsPanel extends BasePanel {
    private final Patient patient;
    private final PharmacyService service;

    private JTable consultationsTable;
    private DefaultTableModel tableModel;
    private JTextArea messageArea;
    private JTextArea historyArea;
    private JButton sendBtn;

    public PatientConsultationsPanel(MainFrame frame) {
        super(frame);
        this.patient = (Patient) frame.getCurrentUser();
        this.service = frame.getPharmacyService();
        initializeComponents();
        loadConsultations();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15,15));
        setBorder(new EmptyBorder(20,20,20,20));

        // Header
        JLabel title = new JLabel("Consultations & Messages", ThemeIcons.MESSAGES, JLabel.LEFT);
        title.setFont(ThemeFonts.BOLD_XXLARGE);
        title.setForeground(ThemeColors.PRIMARY);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeColors.SURFACE);
        header.setBorder(new CompoundBorder(new LineBorder(ThemeColors.BORDER,1), new EmptyBorder(10,15,10,15)));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Doctor", "Date/Time", "Messages", "Status"};
        tableModel = new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        consultationsTable = new StyledTable(tableModel);
        consultationsTable.setRowHeight(28);
        consultationsTable.getSelectionModel().addListSelectionListener(e->{ if(!e.getValueIsAdjusting()) loadMessages();});
        add(new JScrollPane(consultationsTable), BorderLayout.CENTER);

        // Message history + compose panel
        JPanel msgPanel = new JPanel(new BorderLayout(5,5));
        msgPanel.setBackground(ThemeColors.SURFACE);
        msgPanel.setBorder(new CompoundBorder(new LineBorder(ThemeColors.BORDER,1), new EmptyBorder(10,10,10,10)));

        historyArea = new JTextArea(6,40);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        msgPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        messageArea = new JTextArea(3,40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        msgPanel.add(new JScrollPane(messageArea), BorderLayout.SOUTH);
        sendBtn = new StyledButton("Send", ThemeIcons.MESSAGES);
        sendBtn.addActionListener(e->sendMessage());
        msgPanel.add(sendBtn, BorderLayout.EAST);
        add(msgPanel, BorderLayout.SOUTH);
    }

    private void loadConsultations(){
        tableModel.setRowCount(0);
        if(patient.getConsultations()==null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(Consultation c: patient.getConsultations()){
            String doctorName = findDoctorName(c.getDoctorId());
            tableModel.addRow(new Object[]{c.getId(), doctorName, c.getDateTime().format(fmt), c.getMessages().size(), c.getStatus()});
        }
    }

    private Consultation getSelectedConsultation(){
        int row = consultationsTable.getSelectedRow();
        if(row<0) return null;
        int id = (int) tableModel.getValueAt(row,0);
        return patient.getConsultations().stream().filter(c->c.getId()==id).findFirst().orElse(null);
    }

    private void loadMessages(){
        Consultation c = getSelectedConsultation();
        if(c==null) return;
        StringBuilder sb = new StringBuilder();
        for(Message m: c.getMessages()){
            sb.append(m.getTimestamp()).append(" - ")
              .append(m.getSenderId()==patient.getId()?"You":"Doctor").append(": ")
              .append(m.getContent()).append("\n");
        }
        historyArea.setText(sb.toString());
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private void sendMessage(){
        Consultation c = getSelectedConsultation();
        if(c==null){
            JOptionPane.showMessageDialog(this,"Select a consultation first","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String msg = messageArea.getText().trim();
        if(msg.isEmpty()) return;
        int newId = c.getMessages().size()+1;
        Message m = new Message(newId, patient.getId(), c.getDoctorId(), msg, LocalDateTime.now());
        c.addMessage(m);
        messageArea.setText("");
        mainFrame.getPharmacyService().saveDataToFiles();
        loadConsultations();
        consultationsTable.setRowSelectionInterval(tableModel.getRowCount()-1, tableModel.getRowCount()-1);
        JOptionPane.showMessageDialog(this,"Message sent","Success",JOptionPane.INFORMATION_MESSAGE);
    }

    private String findDoctorName(int id){
        for(Doctor d: service.getDoctors()) if(d.getId()==id) return d.getName();
        return "Doctor #"+id;
    }
} 