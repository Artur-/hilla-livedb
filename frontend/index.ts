import '@vaadin/button';
import '@vaadin/grid';
import '@vaadin/notification';
import '@vaadin/text-field';
import { html, LitElement } from 'lit';
import { customElement, state } from 'lit/decorators.js';
import { ItemEndpoint } from './generated/endpoints';
import Item from './generated/org/vaadin/artur/hilla/livedb/Item';
import Operation from './generated/org/vaadin/artur/hilla/livedb/ItemEvent/Operation';

@customElement('db-view')
export class HelloWorldView extends LitElement {
  @state()
  items: Item[] = [];

  connectedCallback() {
    super.connectedCallback();
    ItemEndpoint.getItems().onNext((value) => {
      this.items = [...this.items, value];
    });
    ItemEndpoint.getItemUpdates().onNext((value) => {
      switch (value.operation) {
        case Operation.ADD:
          this.items = [...this.items, value.item!];
          break;
        case Operation.DELETE:
          this.items = this.items.filter((item) => item.id !== value.id);
          break;
        case Operation.UPDATE:
          this.items = this.items.map((item) => (item.id === value.id ? value.item! : item));
      }
    });
  }

  render() {
    return html`
      <vaadin-grid style="height: 100vh" .items=${this.items}>
       <vaadin-grid-column path="id"></vaadin-grid-column>
       <vaadin-grid-column path="quantity"></vaadin-grid-column>
       <vaadin-grid-column path="name"></vaadin-grid-column>
       </vaadin-grid>
    `;
  }
}
