using System;
using System.Collections;
using System.Collections.Generic;
using CommonDomain;
using CommonDomain.Persistence;
using Documently.Commands;
using Documently.Messages;
using MassTransit;
using NUnit.Framework;
using System.Linq;

namespace Documently.Specs
{
	[TestFixture]
	public abstract class CommandTestFixture<TCommand, TCommandHandler, TAggregateRoot>
		where TCommand : class, Command
		where TCommandHandler : class, Consumes<TCommand>.All
		where TAggregateRoot : IAggregate, new()
	{
		protected TAggregateRoot AggregateRoot;
		protected Consumes<TCommand>.All CommandHandler;
		protected Exception CaughtException;
		protected ICollection PublishedEvents;
		protected IEnumerable<DomainEvent> PublishedEventsT { get { return PublishedEvents.Cast<DomainEvent>(); } } 
		protected FakeRepository Repository;
		protected virtual void SetupDependencies() { }

		protected virtual IEnumerable<DomainEvent> Given()
		{
			return new List<DomainEvent>();
		}

		protected virtual void Finally() { }

		protected abstract TCommand When();

		[SetUp]
		public void Setup()
		{
			AggregateRoot = new TAggregateRoot();
			Repository = new FakeRepository(AggregateRoot);
			CaughtException = new ThereWasNoExceptionButOneWasExpectedException();
			foreach (var @event in Given())
			{
				AggregateRoot.ApplyEvent(@event);
			}

			CommandHandler = BuildCommandHandler();
			SetupDependencies();
			try
			{
				CommandHandler.Consume(When());
				if (Repository.SavedAggregate == null)
					PublishedEvents = AggregateRoot.GetUncommittedEvents();
				else
					PublishedEvents = Repository.SavedAggregate.GetUncommittedEvents();
			}
			catch (Exception exception)
			{
				CaughtException = exception;
			}
			finally
			{
				Finally();
			}
		}

		private Consumes<TCommand>.All BuildCommandHandler()
		{
		    Func<IRepository> createReposFunc = () => Repository;
			return Activator.CreateInstance(typeof(TCommandHandler), createReposFunc) as TCommandHandler;
		}
	}

	public class ThereWasNoExceptionButOneWasExpectedException : Exception { }
}