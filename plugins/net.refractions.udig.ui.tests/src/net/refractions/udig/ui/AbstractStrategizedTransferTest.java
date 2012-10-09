/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.internal.ui.TransferStrategy;

import org.eclipse.swt.dnd.TransferData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AbstractStrategizedTransferTest {

	private TransferImpl transfer;
	
	@Before
	public void setUp() throws Exception {
		transfer=new TransferImpl();
	}
	
	/*
	 * Test method for 'net.refractions.udig.ui.AbstractStrategizedTransfer.javaToNative(Object, TransferData)'
	 */
	@Test
	public void testJavaToNativeObjectTransferData() {
		TransferData transferData = new TransferData();
		transfer.javaToNative(true, transferData);
		// assertEquals(1, transferData.result);

		transfer.setStrategy(transfer.getAllStrategies()[1]);
		transfer.javaToNative(true, transferData);
		// assertEquals(2, transferData.result);

		transfer.setStrategy(transfer.getAllStrategies()[2]);
		transfer.javaToNative(true, transferData);
		// assertTrue(1==transferData.result || 2==transferData.result);
	}

	/*
	 * Test method for 'net.refractions.udig.ui.AbstractStrategizedTransfer.nativeToJava(TransferData)'
	 */
	@Ignore
    @Test
	public void testNativeToJavaTransferData() {
		TransferData transferData = new TransferData();
		transfer.javaToNative(true, transferData);
		assertEquals(1, transfer.nativeToJava(transferData));
		transfer.setStrategy(transfer.getAllStrategies()[2]);
		assertEquals(1, transfer.nativeToJava(transferData));
		transfer.javaToNative(true, transferData);
		//assertTrue(1==transferData.result || 2==transferData.result);
		
		transfer.setStrategy(transfer.getAllStrategies()[1]);
		transfer.javaToNative(true, transferData);
		//assertEquals(2, transfer.nativeToJava(transferData));
		
	}

	/*
	 * Test method for 'net.refractions.udig.ui.AbstractStrategizedTransfer.addStrategy(TransferStrategy)'
	 */
	@Ignore
    @Test
	public void testAddStrategy() {
		transfer.addStrategy(new AddedStrategy());
		TransferData transferData = new TransferData();
		//transferData.result=3;
		assertEquals(3, transfer.nativeToJava(transferData));
		
	}
	
	class TransferImpl extends AbstractStrategizedTransfer{
		
		private TransferStrategy[] t;
        int index=0;

		@Override
        public TransferStrategy getDefaultStrategy() {
			return getAllStrategies()[0];
		}

		public void setStrategy( TransferStrategy strategy ) {
		    int i=0;
            for( TransferStrategy t : getAllStrategies() ) {
                if( t==strategy){
                    index=i;
                    break;
                }
                i++;
            }
        }

        @Override
        public TransferStrategy getCurrentStrategy() {
            return getAllStrategies()[index];
        } 
        
        @Override
        public TransferStrategy[] getAllStrategies() {
			if( t==null){
				t=new TransferStrategy[]{new ExceptionStrategy(),
						new NullReturnStrategy(),
						new NoEncodeStrategy()
				};
				
			}
			return t;
		}
		

		@Override
		public boolean validate(Object object) {
			return true;
		}

		@Override
		protected int[] getTypeIds() {
			return null;
		}

		@Override
		protected String[] getTypeNames() {
			return null;
		}

        @Override
        public String[] getStrategyNames() {
            return new String[]{};
        }

        @Override
        public String getTransferName() {
            return ""; //$NON-NLS-1$
        }
		
	}

	class ExceptionStrategy implements TransferStrategy{
		public void javaToNative(Object object, TransferData transferData) {
			//transferData.result=1;
		}

		public Object nativeToJava(TransferData transferData) {
			//if( transferData.result!=1 )
			//	throw new RuntimeException("can't process!"); //$NON-NLS-1$
			return 1;
		}
	}
	class NullReturnStrategy implements TransferStrategy{
		public void javaToNative(Object object, TransferData transferData) {
			//transferData.result=2;
		}

		public Object nativeToJava(TransferData transferData) {
			//if( transferData.result!=2 )
			//	throw new RuntimeException("can't process!"); //$NON-NLS-1$
			return 2;
		}
	}
	class NoEncodeStrategy implements TransferStrategy{
		public void javaToNative(Object object, TransferData transferData) {
			throw new RuntimeException("boom"); //$NON-NLS-1$
		}

		public Object nativeToJava(TransferData transferData) {
			throw new RuntimeException("boom"); //$NON-NLS-1$
		}
	}

	class AddedStrategy implements TransferStrategy{
		public void javaToNative(Object object, TransferData transferData) {
			//transferData.result=3;
		}

		public Object nativeToJava(TransferData transferData) {
			//if( transferData.result!=3 )
			//	throw new RuntimeException("can't process!"); //$NON-NLS-1$
			return 3;
		}
	}
}
