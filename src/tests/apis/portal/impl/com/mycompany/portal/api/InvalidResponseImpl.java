/*
 * $Id: InvalidResponseImpl.java,v 1.3 2007/03/12 10:40:56 agoubard Exp $
 */
package com.mycompany.portal.api;

import org.xins.server.FunctionResult;

/**
 * Implementation of the <code>InvalidResponse</code> function.
 *
 * @version $Revision: 1.3 $ $Date: 2007/03/12 10:40:56 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class InvalidResponseImpl extends InvalidResponse {

   /**
    * Constructs a new <code>InvalidResponseImpl</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public InvalidResponseImpl(APIImpl api) {
      super(api);
   }

   public final Result call(Request request) throws Throwable {

      if (request.isSetErrorCode()) {
         String errorCode = request.getErrorCode();
         return new NaughtyResult(errorCode);
      }

      SuccessfulResult result = new SuccessfulResult();
      result.setPattern("bla");
      return result;
   }

   // XXX: This is a hack!
   private static final class NaughtyResult
   extends FunctionResult
   implements UnsuccessfulResult {

      private NaughtyResult(String errorCode) {
         super(errorCode);
      }
   }
}
