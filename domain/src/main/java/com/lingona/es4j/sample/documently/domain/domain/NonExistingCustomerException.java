﻿using System;

namespace Documently.Domain.Domain
{
	public class NonExistingCustomerException : Exception
	{
		public NonExistingCustomerException(string message) : base(message)
		{
		}
	}
}