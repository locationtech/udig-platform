Login Example
=============

Setting up an UDIGApplication that requires a login is a common request. This example shows how to
use the checkLogin() method to talk to a "Client" plug-in and call its login method.

* :doc:`checkLogin`

* :doc:`LoginDialog`


The "Client' plug-in technique is used when you have a Client plugin that holds on to a Spring
remoting Session with a J2EE server application.

checkLogin
==========

Here is an example of overriding the UDIGApplication checkLogin() method.

::

    public boolean checkLogin() {
            LoginDialog loginDialog = new LoginDialog(
                    "Connecting Communities Login",
                    "Please login to Connecting Communities:" );
            
            boolean login= false;
            while( !login ){
                int result = loginDialog.open();                    
                if( result == MessageDialog.OK){
                    try {
                        login = Client.getDefault().login( loginDialog.getUser(), loginDialog.getPassword() );
                        if( !login ){
                            loginDialog.setMessage("Could not login - please try again");
                        }                    
                    } catch (Exception connectionProblem) {
                        MessageDialog.openInformation(null, "Could not Connect", connectionProblem.toString() );
                        
                        return false; // probably should prompt user here?
                    }
                }
                else {
                    return false; // user cancelled
                }
            }
            return true;
        }

LoginDialog
===========

The above example made use of a really simple MessageDialog shown bellow:

::

    public class LoginDialog extends MessageDialog {
        String user;
        String password;
        public LoginDialog( String title, String dialogMessage) {
            super(null, title, null, dialogMessage, QUESTION,
                    new String[]{"Login","Exit"}, 0 );            
        }

        protected Control createCustomArea(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout( new GridLayout(2,false) );
                        
            Label label = new Label( composite, SWT.RIGHT );
            label.setText("User:");
            GridData gridData = new GridData( SWT.FILL, SWT.FILL, false, false );
            gridData.widthHint = 100; // just a suggestion
            label.setLayoutData( gridData );
                        
            final Text userText = new Text( composite, SWT.SINGLE );
            userText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ));
            userText.addModifyListener( new ModifyListener(){
                public void modifyText( ModifyEvent e ) {
                    user = userText.getText();
                }                
            });
            
            label = new Label( composite, SWT.RIGHT );
            label.setText("Password:");
            label.setLayoutData( gridData );
            
            final Text passwordText = new Text ( composite, SWT.SINGLE | SWT.PASSWORD );
            passwordText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ));
            passwordText.addModifyListener( new ModifyListener(){
                public void modifyText( ModifyEvent e ) {
                    user = passwordText.getText();
                }                
            });
            return null;
        }
        
        public String getUser() {
            return user;
        }
        public String getPassword() {
            return password;
        }
        public void setMessage( String message ){
            messageLabel.setText( message );
        }
    }

