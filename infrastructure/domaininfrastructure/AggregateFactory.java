using System;
using CommonDomain;
using CommonDomain.Persistence;

namespace Documently.Infrastructure.DomainInfrastructure
{
	public class AggregateFactory : IConstructAggregates
	{
		public IAggregate Build(Type type, Guid id, IMemento snapshot)
		{
			//return Activator.CreateInstance(type, id) as IAggregate; 
			// Note: here we could provide some nice AOP support!
			return Activator.CreateInstance(type) as IAggregate;
		}
	}
}