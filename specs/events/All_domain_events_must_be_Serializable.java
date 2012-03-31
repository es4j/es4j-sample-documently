using System;
using System.Linq;
using Documently.Messages;
using Documently.Messages.CustomerEvents;
using NUnit.Framework;

namespace Documently.Specs.Events
{
	[TestFixture]
	public class All_domain_events_must_be_Serializable
	{
		[Test]
		public void All_domain_events_will_have_the_Serializable_attribute_assigned()
		{
			var domainEventTypes = typeof(Created).Assembly.GetExportedTypes().Where(x => x.BaseType == typeof(DomainEvent)).ToList();
			foreach (var domainEventType in domainEventTypes)
			{
				if (domainEventType.IsSerializable)
					continue;

				throw new Exception(string.Format("Domain event '{0}' is not Serializable", domainEventType.FullName));
			}
		}
	}
}