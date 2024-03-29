package org.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;

/**
 * Class for the connection dialog
 * @author twk
 */
public class ConnectView extends javax.swing.JFrame {

    /** Creates new form ConnectView */
    public ConnectView(PapinhoView mv) {
        initComponents();
        mainView = mv;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bOk = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfHost = new javax.swing.JTextField();
        tfPort = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        bOk.setMnemonic('O');
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.client.PapinhoApp.class).getContext().getResourceMap(ConnectView.class);
        bOk.setText(resourceMap.getString("bOk.text")); // NOI18N
        bOk.setName("bOk"); // NOI18N
        bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOkActionPerformed(evt);
            }
        });

        bCancel.setText(resourceMap.getString("bCancel.text")); // NOI18N
        bCancel.setName("bCancel"); // NOI18N
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        tfHost.setText(resourceMap.getString("tfHost.text")); // NOI18N
        tfHost.setName("tfHost"); // NOI18N

        tfPort.setText(resourceMap.getString("tfPort.text")); // NOI18N
        tfPort.setName("tfPort"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfPort, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfHost, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bOk, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(tfPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bOk)
                    .addComponent(bCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Gnadler ofr the OK button, establishes the connection with the server
     * @param evt
     */
    private void bOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOkActionPerformed
        String host = tfHost.getText();
        String port = tfPort.getText();
        try {
            PapinhoApp.getApplication().getRemoteServerObject(host, port);
            mainView.getmConnect().setVisible(false);
            mainView.getmDisconnect().setVisible(true);
            mainView.getTaInput().setEnabled(true);
            dispose();
        } catch (RemoteException rEx) {
            System.out.println("Error!");

            rEx.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    rEx.getMessage(),
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NotBoundException nbEx) {
            nbEx.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "There is no server running on the specified host and port",
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bOkActionPerformed

    /**
     * Handler for the Cancel button, disposes of the window
     * @param evt
     */
    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        dispose();
    }//GEN-LAST:event_bCancelActionPerformed
    private PapinhoView mainView;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfPort;
    // End of variables declaration//GEN-END:variables
}
