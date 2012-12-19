using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TeamBot.Communication
{

    class Constants
    {
        public const int TB_COMMAND_ID			  	  			 =	0x00;
        //------------------------------------------------------
        public const int TB_COMMAND_REQUESTSTATE_VELOCITYDRIVE   =  0x00;
        public const int TB_COMMAND_REQUESTSTATE_POSITIONDRIVE	 =	0x01;
        public const int TB_COMMAND_REQUESTSTATE_TURN			 =	0x02;
        public const int TB_COMMAND_REQUESTSTATE_STOP			 =	0x03;
        public const int TB_COMMAND_RESET			  			 =	0x04;
        //======================================================


        public const int TB_VELOCITY_ID				  		    =	0x01;
        //------------------------------------------------------
        public const int TB_VELOCITY_FORWARD			  		=	0x00; //Target Velocity
        public const int TB_VELOCITY_BACKWARD	      			=	0x01; //Target Velocity
        public const int TB_VELOCITY_TURN_LEFT					=	0x02; //Right Forwards | Left Backwards
        public const int TB_VELOCITY_TURN_RIGHT				    =	0x03; //Left Forwards  | Right Backwards
        //======================================================


        public const int TB_TURN_ID					  		    =	0x03;
        //------------------------------------------------------
        public const int TB_TURN_RIGHT				  			=	0x00;
        public const int TB_TURN_LEFT				  			=	0x01;
        //======================================================


        public const int TB_POSITION_ID				  		    =	0x04;
        //------------------------------------------------------
        public const int TB_POSITION_GLOBAL                     =   0x00;
        public const int TB_POSITION_LOCAL  			  	    =	0x01;
        //======================================================

        public const int TB_DATA_ID								=	0x05;
        //------------------------------------------------------
        public const int TB_DATA_INFRARED                       =   0x01;  //char distance left | char distance middle | char distance right
        //======================================================


        public const int TB_ERROR_ID					  		=	0x42;
        //------------------------------------------------------
        public const int TB_ERROR_TRACE						    =	0x00;
        public const int TB_ERROR_DEBUG						    =	0x01;
        public const int TB_ERROR_LOG							=	0x02;
        public const int TB_ERROR_INFO							=	0x03;
        public const int TB_ERROR_ERROR						    =	0x04;
        //======================================================
    }


    class TBFrame
    {
        public byte Id; //defined above
        public byte SubId;
        public ushort TimeStamp; //in 10ms
    };

    class TBVelocity : TBFrame {
        public byte speedLeft;
        public byte speedRight;
    };

    class TBInfraredData : TBFrame
    {
        public byte leftSpeed;
        public byte middletSpeed;
        public byte rightSpeed;
    }

    interface IData
    {
        void receive(TBFrame vel);
        void sendData(TBFrame data);
    }
}
