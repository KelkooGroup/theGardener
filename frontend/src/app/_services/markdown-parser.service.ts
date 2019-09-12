import {Injectable} from '@angular/core';
import {ExternalLinkPart, MarkdownPart, MarkdownSettings, PagePart, ScenarioPart} from '../_models/hierarchy';

@Injectable({
  providedIn: 'root'
})
export class MarkdownParserService {

  constructor() {
  }

  parseMarkdown(markdown: string): Array<PagePart> {
    const parts = this.extractMarkdownParts(markdown);
    const pageParts: Array<PagePart> = parts.map(p => {
      if (p.startsWith(THE_GARDENER_START)) {
        const settings = this.parseTheGardenerSettings(p);
        if (settings.include) {
          const externalPagePart: ExternalLinkPart = {
            type: 'ExternalLink',
            externalLink: settings.include ? settings.include.url : '',
          };
          return externalPagePart;
        } else if (settings.scenarios) {
          const scenarioPagePart: ScenarioPart = {
            type: 'Scenario',
            scenarioSettings: settings.scenarios
          };
          return scenarioPagePart;
        } else {
          // TODO handle error
          return undefined;
        }
      } else {
        const mdPart: MarkdownPart = {
          type: 'Markdown',
          markdown: p,
        };
        return mdPart;
      }
    });
    return pageParts;
  }

  parseTheGardenerSettings(theGardener: string): MarkdownSettings {
    const settingsString = theGardener
      .replace(THE_GARDENER_START, '')
      .replace(THE_GARDENER_END, '');
    const settings = JSON.parse(settingsString) as MarkdownSettings;
    return settings;
  }

  private extractMarkdownParts(markdown: string): Array<string> {
    const parts: Array<string> = [];
    let stringRest = markdown;
    let nextTheGardenerStartIndex = stringRest.indexOf(THE_GARDENER_START);

    while (nextTheGardenerStartIndex !== -1) {
      let startIndex = nextTheGardenerStartIndex;
      let stopIndex;
      const escapingStartIndex = stringRest.indexOf(ESCAPING_SENTENCE);
      if (escapingStartIndex !== -1 && escapingStartIndex < nextTheGardenerStartIndex) {
        // theGardener syntax is escaped
        startIndex = stringRest.indexOf(ESCAPING_SENTENCE);
        const escapingEndIndex = stringRest.indexOf(ESCAPING_SENTENCE, nextTheGardenerStartIndex + ESCAPING_SENTENCE.length);
        if (escapingEndIndex === -1) {
          // TODO handle error
        } else {
          stopIndex = escapingEndIndex + ESCAPING_SENTENCE.length;
        }
      } else {
        const theGardenerStopIndex = stringRest.indexOf(THE_GARDENER_END, nextTheGardenerStartIndex + THE_GARDENER_START.length);
        if (theGardenerStopIndex === -1) {
          // TODO handle error
        } else {
          stopIndex = theGardenerStopIndex + THE_GARDENER_END.length;
        }
      }
      const before = stringRest.substr(0, startIndex);
      const part = stringRest.substr(startIndex, stopIndex - startIndex);
      parts.push(before);
      parts.push(part);
      stringRest = stringRest.substr(stopIndex);
      nextTheGardenerStartIndex = stringRest.indexOf(THE_GARDENER_START);
    }
    parts.push(stringRest);
    return parts.filter(s => s !== '');
  }
}

const ESCAPING_SENTENCE = '````';
const THE_GARDENER_START = '```thegardener';
const THE_GARDENER_END = '```';
