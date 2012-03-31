using Castle.MicroKernel.Registration;
using Castle.MicroKernel.SubSystems.Configuration;
using Castle.Windsor;
using CommonDomain;
using CommonDomain.Core;
using CommonDomain.Persistence;
using CommonDomain.Persistence.EventStore;
using Documently.Infrastructure.DomainInfrastructure;
using EventStore;
using EventStore.Dispatcher;
using EventStore.Serialization;

namespace Documently.Infrastructure.Installers
{
	/// <summary>
	/// Installs Jonathan Oliver's Event Store with a JsonSerializer and an asynchronous dispatcher.
	/// </summary>
	public class EventStoreInstaller : IWindsorInstaller
	{
		private readonly byte[] _EncryptionKey = new byte[]
		{
			0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 
			0xa, 0xb, 0xc, 0xd, 0xe, 0xf
		};

		public void Install(IWindsorContainer container, IConfigurationStore store)
		{
			container.Register(
				Component.For<IStoreEvents>()
					.UsingFactoryMethod(k => GetInitializedEventStore(k.Resolve<IDispatchCommits>())),
				C<IRepository, EventStoreRepository>(),
				C<IConstructAggregates, AggregateFactory>(),
				C<IDetectConflicts, ConflictDetector>());
		}

		private static ComponentRegistration<TS> C<TS, TC>() where TC : TS
			where TS:class
		{
			return Component.For<TS>().ImplementedBy<TC>().LifeStyle.Transient;
		}

		private IStoreEvents GetInitializedEventStore(IDispatchCommits bus)
		{
			return Wireup.Init()
				.UsingAsynchronousDispatchScheduler(bus)
				.UsingRavenPersistence(Keys.RavenDbConnectionStringName)
					.ConsistentQueries()
					.PageEvery(int.MaxValue)
					.MaxServerPageSizeConfiguration(1024)
				.Build();
		}

		// possibility to encrypt everything stored
		private ISerialize BuildSerializer()
		{
			var serializer = new JsonSerializer() as ISerialize;
			serializer = new GzipSerializer(serializer);
			return new RijndaelSerializer(serializer, _EncryptionKey);
		}
	}
}