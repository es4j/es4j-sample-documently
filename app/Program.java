﻿using System;
using System.Net;
using Castle.MicroKernel.Registration;
using Castle.Windsor;
using Documently.Commands;
using Documently.Infrastructure;
using Documently.Infrastructure.Installers;
using Documently.ReadModel;
using Magnum;
using MassTransit;
using NLog;
using NLog.Config;
using Raven.Client;

namespace Documently.App
{
	internal class Program
	{
		private static readonly Logger _logger = LogManager.GetCurrentClassLogger();
		
		private IWindsorContainer _container;

		private static void Main()
		{
			Description();
			SimpleConfigurator.ConfigureForConsoleLogging();

			var p = new Program();
			try { p.Start(); }
			finally { p.Stop(); }
		}

		private void Start()
		{
			try
			{
				_logger.Info("installing and setting up components");
				_container = new WindsorContainer()
					.Install(
						new RavenDbServerInstaller(),
						new ReadRepositoryInstaller(),
						new BusInstaller("rabbitmq://localhost/Documently.App"),
						new EventStoreInstaller());

				_container.Register(Component.For<IWindsorContainer>().Instance(_container));

				var customerId = CombGuid.Generate();

				Console.WriteLine("create new customer by pressing a key");
				Console.ReadKey(true);

				//create customer (Write/Command)
				CreateCustomer(customerId);

				Console.WriteLine("Customer created. Press any key to relocate customer.");
				Console.ReadKey(true);

				//Customer relocating (Write/Command)
				RelocateCustomer(customerId);

				Console.WriteLine("Customer relocated. Press any key to show list of customers.");
				Console.ReadKey(true);

				//show all customers [in RMQ] (Read/Query)
				ShowCustomerListView();
			}
			catch (WebException ex)
			{
				_logger.Error(@"Unable to connect to RavenDB Server. Have you started 'RavenDB\Server\Raven.Server.exe'?", ex);
			}
			catch (Exception ex)
			{
				_logger.Error("exception thrown", ex);
			}

			Console.WriteLine("Press any key to finish.");
			Console.ReadKey(true);
		}

		private static void Description()
		{
			Console.WriteLine(@"This application:
* Creates a customer
* Relocates the customer
* Shows all customers.");
		}

		private void ShowCustomerListView()
		{
			var store = _container.Resolve<IDocumentStore>();

			using (var session = store.OpenSession())
			{
				foreach (var dto in session.Query<CustomerListDto>())
				{
					Console.WriteLine(dto.Name + " now living in " + dto.City + " (" + dto.AggregateRootId + ")");
					Console.WriteLine("---");
				}
			}
		}

		private void CreateCustomer(Guid aggregateId)
		{
			GetDomainService()
				.Send(new CreateNewCustomer(aggregateId, "Jörg Egretzberger", "Meine Straße", "1", "1010", "Wien", "01/123456"));
		}

		private void RelocateCustomer(Guid customerId)
		{
			GetDomainService()
				.Send(new RelocateTheCustomer(customerId, "Messestraße", "2", "4444", "Linz"));
		}

		private IEndpoint GetDomainService()
		{
			var bus = _container.Resolve<IServiceBus>();
			var domainService = bus.GetEndpoint(new Uri(Keys.DomainServiceEndpoint));
			return domainService;
		}

		private void Stop()
		{
			_container.Dispose();
		}
	}
}