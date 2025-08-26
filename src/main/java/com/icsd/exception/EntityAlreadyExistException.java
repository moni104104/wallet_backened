package com.icsd.exception;

public class EntityAlreadyExistException extends RuntimeException 
{
	public EntityAlreadyExistException()
	{
		
	}
	public EntityAlreadyExistException(String msg)
	{
		super(msg);
	}

}
