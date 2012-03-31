using System;
using Caliburn.Micro;
using Documently.Commands;
using Documently.Infrastructure;
using Documently.ReadModel;
using Documently.WpfClient.ApplicationFramework;
using MassTransit;

namespace Documently.WpfClient.Modules.CustomerDetails.CustomerRelocating
{
	public class CustomerRelocatingViewModel : ScreenWithValidatingCommand<RelocateTheCustomer>
	{
		private readonly IBus _bus;
		private readonly IEventAggregator _eventAggregator;
		private readonly IReadRepository _readRepository;

		public CustomerRelocatingViewModel(IBus bus, IEventAggregator eventAggregator, IReadRepository readRepository)
		{
			_bus = bus;
			_eventAggregator = eventAggregator;
			_readRepository = readRepository;

			//TODO: Resolve through IoC Container
			Validator = new RelocatingCustomerValidator();
		}

		public void WithCustomer(Guid customerId)
		{
			ViewModel = _readRepository.GetById<CustomerAddressDto>(Dto.GetDtoIdOf<CustomerAddressDto>(customerId));
			Command = new ValidatingCommand<RelocateTheCustomer>(new RelocateTheCustomer(ViewModel.AggregateRootId, ViewModel.Street, ViewModel.StreetNumber, ViewModel.PostalCode, ViewModel.City),
			                                                         Validator);
		}

		public CustomerAddressDto ViewModel { get; private set; }

		public string Address
		{
			get
			{
				return string.Format("{0} {1}, {2} {3}", ViewModel.Street, ViewModel.StreetNumber, ViewModel.PostalCode,
				                     ViewModel.City);
			}
		}

		public void Save()
		{
			if (!Validate().IsValid)
				return;

			//important: send command over bus
			_bus.Send(Command.InnerCommand);

			//signal for UI - change view
			_eventAggregator.Publish(new CustomerRelocatingSavedEvent());
		}
	}


	public class CustomerRelocatingSavedEvent
	{
	}
}