using System;
using System.Linq;
using Documently.Commands;
using NUnit.Framework;

namespace Documently.Specs.Commands
{
	[TestFixture]
	public class All_commands_must_be_Serializable
	{
		[Test]
		public void All_commands_will_have_the_Serializable_attribute_assigned()
		{
			var commands = typeof(Command).Assembly.GetExportedTypes().Where(x => x.BaseType == typeof(Command)).ToList();
			foreach (var commandType in commands)
			{
				if (commandType.IsSerializable) continue;
				throw new Exception(string.Format("Command '{0}' is not Serializable", commandType.FullName));
			}
		}
	}
}